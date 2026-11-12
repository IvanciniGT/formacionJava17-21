package com.curso.diccionario.service.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.ServerSocket;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.service.api.BuscadorDePalabrasException;
import com.curso.diccionario.service.api.IdiomaNoSoportado;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.api.PalabraNoEncontrada;

// Lo que el contrato del servicio no puede probar porque el servidor real nunca lo hace: errores,
// respuestas raras, servidor caído. Que el remoto cumple el contrato contra el servidor de verdad
// se prueba en diccionario-app-servidor (BuscadorRemotoCumpleContratoTest).
class BuscadorDePalabrasRemotoTest {

    static final String DICCIONARIOS = "/api/v1/diccionarios";
    static final String IDIOMA_NO_DISPONIBLE = "{\"codigo\":\"IDIOMA_NO_DISPONIBLE\",\"mensaje\":\"No hay\"}";
    static final String PALABRA_NO_ENCONTRADA = "{\"codigo\":\"PALABRA_NO_ENCONTRADA\",\"mensaje\":\"No está\"}";
    static final String ERROR_INTERNO = "{\"codigo\":\"ERROR_INTERNO\",\"mensaje\":\"Fallo\"}";

    ServidorFalso servidor;
    BuscadorDePalabrasRemoto buscador;

    @BeforeEach
    void arrancar() throws Exception {
        servidor = new ServidorFalso();
        buscador = new BuscadorDePalabrasRemoto(servidor.url());
    }

    @AfterEach
    void parar() {
        servidor.close();
    }

    @Test
    @DisplayName("Contexto: el servidor anuncia ES y EN | Acción: pedir los idiomas | Resultado esperado: ES y EN")
    void leeLosIdiomas() throws Exception {
        servidor.responde(DICCIONARIOS, 200, "[\"ES\",\"EN\"]");

        assertEquals(List.of("ES", "EN"), buscador.idiomasDisponibles());
    }

    @Test
    @DisplayName("Contexto: el servidor responde 500 a los idiomas | Acción: pedirlos | Resultado esperado: excepción, no una lista vacía")
    void unErrorAlPedirLosIdiomasSeAvisa() {
        servidor.responde(DICCIONARIOS, 500, ERROR_INTERNO);

        assertThrows(BuscadorDePalabrasException.class, () -> buscador.idiomasDisponibles());
    }

    @Test
    @DisplayName("Contexto: 200 para ES y 404 IDIOMA_NO_DISPONIBLE para XX | Acción: preguntar por los dos | Resultado esperado: sí y no")
    void idiomaSoportado() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES", 200, "{\"idioma\":\"ES\"}");
        servidor.responde(DICCIONARIOS + "/XX", 404, IDIOMA_NO_DISPONIBLE);

        assertTrue(buscador.idiomaSoportado("ES"));
        assertFalse(buscador.idiomaSoportado("XX"));
    }

    @Test
    @DisplayName("Contexto: 200 con atún y sus significados | Acción: buscar Atun ignorando | Resultado esperado: encontrada como atún, con sus ejemplos")
    void unaPalabraEncontrada() throws Exception {
        // Java 15: bloque de texto (triple comilla), un String de varias líneas sin \n ni +.
        servidor.responde(DICCIONARIOS + "/es/palabras/Atun", 200, """
                {"idioma":"ES","palabra":"atún","significados":[
                  {"texto":"Pez de gran tamaño.","ejemplos":["Un bocadillo de atún"]},
                  {"texto":"Persona torpe."}]}""");

        PalabraEncontrada encontrada = (PalabraEncontrada) buscador.buscarIgnorandoMayusculasYAcentos("Atun", "es");

        assertEquals("atún", encontrada.palabra());
        assertEquals("ES", encontrada.idioma());
        assertEquals(List.of("Un bocadillo de atún"), encontrada.significados().get(0).getEjemplos());
        // Sin el campo en el JSON: lista vacía, nunca null.
        assertEquals(List.of(), encontrada.significados().get(1).getEjemplos());
        assertTrue(servidor.consultasPedidas.contains("ignorarMayusculasYAcentos=true"), servidor.consultasPedidas.toString());
    }

    @Test
    @DisplayName("Contexto: los dos 404 | Acción: buscar | Resultado esperado: no encontrada o idioma no soportado, según el código")
    void losDos404() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES/palabras/archilococo", 404, PALABRA_NO_ENCONTRADA);
        servidor.responde(DICCIONARIOS + "/ELF/palabras/eldeanor", 404, IDIOMA_NO_DISPONIBLE);

        assertEquals(new PalabraNoEncontrada("archilococo", "ES"), buscador.buscarExacta("archilococo", "ES"));
        assertEquals(new IdiomaNoSoportado("eldeanor", "ELF"), buscador.buscarExacta("eldeanor", "ELF"));
    }

    @Test
    @DisplayName("Contexto: un 404 sin nuestro código (servidor mal configurado) | Acción: buscar | Resultado esperado: excepción, no 'no encontrada'")
    void un404QueNoEsNuestroEsUnError() {
        // El ServidorFalso responde 404 en HTML a cualquier ruta que no conozca.
        assertThrows(BuscadorDePalabrasException.class, () -> buscador.buscarExacta("melón", "ES"));
    }

    @Test
    @DisplayName("Contexto: el servidor responde 500 | Acción: buscar | Resultado esperado: excepción")
    void un500EsUnError() {
        servidor.responde(DICCIONARIOS + "/ES/palabras/melón", 500, ERROR_INTERNO);

        assertThrows(BuscadorDePalabrasException.class, () -> buscador.buscarExacta("melón", "ES"));
    }

    @Test
    @DisplayName("Contexto: una palabra con tilde y espacio | Acción: buscarla | Resultado esperado: viaja codificada en la URL, con %20 y no +")
    void codificaLaPalabraEnLaUrl() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES/palabras/media naranja", 404, PALABRA_NO_ENCONTRADA);

        buscador.buscarExacta("media naranja", "ES");

        assertTrue(servidor.rutasPedidasSinDecodificar.contains(DICCIONARIOS + "/ES/palabras/media%20naranja"),
                servidor.rutasPedidasSinDecodificar.toString());
    }

    @Test
    @DisplayName("Contexto: no hay servidor escuchando | Acción: buscar | Resultado esperado: excepción que dice que no pudo conectar")
    void sinServidor() throws Exception {
        int puertoLibre;
        try (ServerSocket socket = new ServerSocket(0)) {
            puertoLibre = socket.getLocalPort();
        }
        BuscadorDePalabrasRemoto sinServidor = new BuscadorDePalabrasRemoto("http://localhost:" + puertoLibre);

        BuscadorDePalabrasException error = assertThrows(BuscadorDePalabrasException.class, () -> sinServidor.buscarExacta("melón", "ES"));
        assertTrue(error.getMessage().startsWith("No se pudo conectar"), error.getMessage());
    }

    @Test
    @DisplayName("Contexto: 404 de palabra con sugerencias en el cuerpo | Acción: buscar manana | Resultado esperado: no encontrada, con sus sugerencias")
    void lasSugerenciasVienenEnEl404() throws Exception {
        servidor.responde(DICCIONARIOS + "/ES/palabras/manana", 404,
                "{\"codigo\":\"PALABRA_NO_ENCONTRADA\",\"mensaje\":\"No está\",\"sugerencias\":[\"mañana\",\"manzana\"]}");

        assertEquals(new PalabraNoEncontrada("manana", "ES", List.of("mañana", "manzana")), buscador.buscarExacta("manana", "ES"));
    }
}
