package com.curso.diccionario.service.contrato;

import static com.curso.diccionario.api.contrato.SignificadoDePrueba.significado;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.contrato.ContratoBase;
import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.api.contrato.SignificadoDePrueba;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.api.IdiomaNoSoportado;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.api.PalabraNoEncontrada;
import com.curso.diccionario.service.api.ResultadoDeBusqueda;

/**
 * Caja negra: lo que cualquier BuscadorDePalabras tiene que cumplir, sea el que lee los
 * diccionarios aquí mismo o el que se lo pregunta al servidor. Si los dos pasan estas pruebas,
 * la app de consola puede usar uno u otro sin notar la diferencia.
 */
public abstract class ContratoBuscadorDePalabrasTest {

    // Los datos de las pruebas de contrato del API de diccionarios: el mismo "Dado que...".
    static final SignificadoDePrueba MELON_FRUTO = ContratoBase.MELON_FRUTO;
    static final SignificadoDePrueba PERA_FRUTO = ContratoBase.PERA_FRUTO;

    protected abstract BuscadorDePalabras crearBuscadorCon(DatosDePrueba datos) throws Exception;

    private BuscadorDePalabras buscador() throws Exception {
        return crearBuscadorCon(ContratoBase.datosHabituales()
                .conPalabra("ES", "atún", significado("Pez de gran tamaño."))
                .conPalabra("ES", "año", significado("Doce meses."))
                .conPalabra("ES", "ano", significado("Orificio del recto.")));
    }

    private static void esEncontrada(String palabra, String idioma, ResultadoDeBusqueda resultado) {
        PalabraEncontrada encontrada = assertInstanceOf(PalabraEncontrada.class, resultado);
        assertEquals(palabra, encontrada.palabra());
        assertEquals(idioma, encontrada.idioma());
        assertFalse(encontrada.significados().isEmpty());
    }

    // ----------------------------------------------------------------- idiomas

    @Test
    @DisplayName("Contexto: ES y EN | Acción: pedir los idiomas | Resultado esperado: ES y EN")
    void idiomasDisponibles() throws Exception {
        // Java 10: List.copyOf / Map.copyOf, copia inmutable (y si ya lo era, no copia nada).
        assertEquals(Set.of("ES", "EN"), Set.copyOf(buscador().idiomasDisponibles()));
    }

    @Test
    @DisplayName("Contexto: ES | Acción: preguntar por 'es' y por ELF | Resultado esperado: sí y no")
    void idiomaSoportadoSinImportarMayusculas() throws Exception {
        assertTrue(buscador().idiomaSoportado("es"));
        assertFalse(buscador().idiomaSoportado("ELF"));
    }

    // ------------------------------------------------------------- buscarExacta

    @Test
    @DisplayName("Contexto: ES con melón | Acción: buscar exacta melón | Resultado esperado: encontrada, con sus significados")
    void exactaEncuentraLaPalabra() throws Exception {
        PalabraEncontrada encontrada = assertInstanceOf(PalabraEncontrada.class, buscador().buscarExacta("melón", "ES"));

        assertEquals("melón", encontrada.palabra());
        assertEquals("ES", encontrada.idioma());
        assertEquals(List.of(MELON_FRUTO, ContratoBase.MELON_PERSONA), encontrada.significados().stream().map(SignificadoDePrueba::de).toList());
    }

    @Test
    @DisplayName("Contexto: ES con melón | Acción: buscar exacta MELÓN y melon | Resultado esperado: no encontradas: la exacta respeta mayúsculas y acentos")
    void exactaRespetaMayusculasYAcentos() throws Exception {
        assertInstanceOf(PalabraNoEncontrada.class, buscador().buscarExacta("MELÓN", "ES"));
        assertInstanceOf(PalabraNoEncontrada.class, buscador().buscarExacta("melon", "ES"));
    }

    @Test
    @DisplayName("Contexto: ES con melón | Acción: buscar exacta melón en 'es' | Resultado esperado: encontrada: el idioma es un código")
    void exactaNormalizaElIdioma() throws Exception {
        esEncontrada("melón", "ES", buscador().buscarExacta("melón", "es"));
    }

    @Test
    @DisplayName("Contexto: ES sin archilococo | Acción: buscarla | Resultado esperado: no encontrada, con lo que se preguntó")
    void exactaNoEncontrada() throws Exception {
        assertEquals(new PalabraNoEncontrada("archilococo", "es", List.of()), buscador().buscarExacta("archilococo", "es"));
    }

    @Test
    @DisplayName("Contexto: sin élfico | Acción: buscar en elf | Resultado esperado: idioma no soportado, con lo que se preguntó")
    void idiomaNoSoportado() throws Exception {
        assertEquals(new IdiomaNoSoportado("eldeanor", "elf"), buscador().buscarExacta("eldeanor", "elf"));
        assertEquals(new IdiomaNoSoportado("eldeanor", "elf"), buscador().buscarIgnorandoMayusculasYAcentos("eldeanor", "elf"));
    }

    // ----------------------------------------------- buscarIgnorandoMayusculasYAcentos

    @Test
    @DisplayName("Contexto: ES con melón y atún | Acción: buscar MELON y Atun | Resultado esperado: melón y atún, como están en el diccionario")
    void ignorandoEncuentraConOtrasMayusculasYAcentos() throws Exception {
        esEncontrada("melón", "ES", buscador().buscarIgnorandoMayusculasYAcentos("MELON", "ES"));
        esEncontrada("atún", "ES", buscador().buscarIgnorandoMayusculasYAcentos(" Atun ", "es"));
    }

    @Test
    @DisplayName("Contexto: ES con año y ano | Acción: buscar AÑO y ano | Resultado esperado: cada una la suya: la ñ no es un acento")
    void ignorandoRespetaLaEñe() throws Exception {
        esEncontrada("año", "ES", buscador().buscarIgnorandoMayusculasYAcentos("AÑO", "ES"));
        esEncontrada("ano", "ES", buscador().buscarIgnorandoMayusculasYAcentos("ano", "ES"));
    }

    @Test
    @DisplayName("Contexto: ES sin archilococo | Acción: buscarla ignorando | Resultado esperado: no encontrada, con lo que se preguntó")
    void ignorandoNoEncontrada() throws Exception {
        assertEquals(new PalabraNoEncontrada("ARCHILOCOCO", "ES", List.of()), buscador().buscarIgnorandoMayusculasYAcentos("ARCHILOCOCO", "ES"));
    }

    // ------------------------------------------------------------------ sugerencias

    @Test
    @DisplayName("Contexto: ES con melón y pera | Acción: buscar exacta melon | Resultado esperado: no encontrada, con melón como sugerencia")
    void unaPalabraNoEncontradaLlevaLasParecidas() throws Exception {
        assertEquals(new PalabraNoEncontrada("melon", "ES", List.of("melón")), buscador().buscarExacta("melon", "ES"));
    }

    @Test
    @DisplayName("Contexto: ES con pera a 1 letra, y pena y perla a 2, de pira | Acción: buscar pira | Resultado esperado: pera, pena, perla")
    void lasSugerenciasVanDeLaMasALaMenosParecida() throws Exception {
        BuscadorDePalabras buscador = crearBuscadorCon(new DatosDePrueba()
                .conPalabra("ES", "perla", significado("Bola de nácar."))
                .conPalabra("ES", "pena", significado("Tristeza."))
                .conPalabra("ES", "pera", PERA_FRUTO));

        assertEquals(new PalabraNoEncontrada("pira", "ES", List.of("pera", "pena", "perla")), buscador.buscarExacta("pira", "ES"));
    }

    @Test
    @DisplayName("Contexto: ES | Acción: buscar algo que no se parece a nada | Resultado esperado: no encontrada, con la lista de sugerencias vacía")
    void sinNadaParecidoLaListaVaVacia() throws Exception {
        assertEquals(new PalabraNoEncontrada("archilococo", "ES", List.of()), buscador().buscarExacta("archilococo", "ES"));
    }
}
