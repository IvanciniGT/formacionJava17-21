package com.curso.diccionario.controlador.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.api.BuscadorDePalabrasException;
import com.curso.diccionario.service.api.IdiomaNoSoportado;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.api.PalabraNoEncontrada;

// Por HTTP simulado y no llamando a los métodos: lo que se prueba es lo que ve un cliente
// (URLs heredadas del interfaz, status, JSON), y eso solo existe con Spring MVC de por medio.
// El servicio es falso: aquí no se prueba la búsqueda, solo cómo se expone.
@WebMvcTest(ControladorRestDiccionarios.class)
class ControladorRestDiccionariosTest {

    // @WebMvcTest busca hacia arriba una configuración de Spring Boot; esta librería no tiene.
    @SpringBootApplication
    static class Configuracion {}

    // Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
    record SignificadoDePrueba(String getTexto, List<String> getEjemplos) implements Significado {}

    @Autowired
    MockMvc mvc;

    @MockitoBean
    BuscadorDePalabras buscador;

    @Test
    void listaLosIdiomas() throws Exception {
        when(buscador.idiomasDisponibles()).thenReturn(List.of("ES", "EN"));

        mvc.perform(get("/api/v1/diccionarios"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"ES\",\"EN\"]", true));
    }

    @Test
    void unErrorAlListarLosIdiomasDa500YNoUnaListaVacia() throws Exception {
        when(buscador.idiomasDisponibles()).thenThrow(new BuscadorDePalabrasException("fallo", new IOException("carpeta ilegible")));

        mvc.perform(get("/api/v1/diccionarios"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"));
    }

    @Test
    void confirmaUnIdiomaDisponible() throws Exception {
        when(buscador.idiomaSoportado("ES")).thenReturn(true);

        mvc.perform(get("/api/v1/diccionarios/ES"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"idioma\":\"ES\"}", true));
    }

    @Test
    void unIdiomaSinDiccionarioDa404() throws Exception {
        when(buscador.idiomaSoportado("XX")).thenReturn(false);

        mvc.perform(get("/api/v1/diccionarios/XX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("IDIOMA_NO_DISPONIBLE"));
    }

    @Test
    void devuelveLosSignificadosDeUnaPalabra() throws Exception {
        when(buscador.buscarExacta("melón", "ES")).thenReturn(new PalabraEncontrada("melón", "ES", List.of(
                new SignificadoDePrueba("Fruto grande.", List.of("Me gusta el melón")),
                new SignificadoDePrueba("Persona con pocas luces.", null))));

        // Con tilde: la palabra viaja codificada en la URL y tiene que llegar decodificada.
        mvc.perform(get("/api/v1/diccionarios/ES/palabras/melón"))
                .andExpect(status().isOk())
                // Java 15: bloque de texto (triple comilla), un String de varias líneas sin \n ni +.
                .andExpect(content().json("""
                        {"idioma":"ES","palabra":"melón","significados":[
                          {"texto":"Fruto grande.","ejemplos":["Me gusta el melón"]},
                          {"texto":"Persona con pocas luces.","ejemplos":[]}]}""", true));
    }

    @Test
    void conIgnorarMayusculasYAcentosUsaLaOtraBusquedaYDevuelveLaPalabraDelDiccionario() throws Exception {
        when(buscador.buscarIgnorandoMayusculasYAcentos("Atun", "es"))
                .thenReturn(new PalabraEncontrada("atún", "ES", Arrays.asList(new SignificadoDePrueba("Pez.", List.of()))));

        mvc.perform(get("/api/v1/diccionarios/es/palabras/Atun").param("ignorarMayusculasYAcentos", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.palabra").value("atún"))
                .andExpect(jsonPath("$.idioma").value("ES"));
        verify(buscador).buscarIgnorandoMayusculasYAcentos("Atun", "es");
    }

    @Test
    void buscarEnUnIdiomaSinDiccionarioDa404DeIdioma() throws Exception {
        when(buscador.buscarExacta("melón", "XX")).thenReturn(new IdiomaNoSoportado("melón", "XX"));

        mvc.perform(get("/api/v1/diccionarios/XX/palabras/melón"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("IDIOMA_NO_DISPONIBLE"));
    }

    @Test
    void unaPalabraQueNoEstaDa404DePalabra() throws Exception {
        when(buscador.buscarExacta("melón", "ES")).thenReturn(new PalabraNoEncontrada("melón", "ES"));

        mvc.perform(get("/api/v1/diccionarios/ES/palabras/melón"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("PALABRA_NO_ENCONTRADA"));
    }

    @Test
    void unErrorEnLaBusquedaDa500SinDetallesInternos() throws Exception {
        when(buscador.buscarExacta("melón", "ES"))
                .thenThrow(new BuscadorDePalabrasException("fallo", new IOException("/ruta/secreta/es.txt")));

        mvc.perform(get("/api/v1/diccionarios/ES/palabras/melón"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(content().string(not(containsString("secreta"))));
    }
}
