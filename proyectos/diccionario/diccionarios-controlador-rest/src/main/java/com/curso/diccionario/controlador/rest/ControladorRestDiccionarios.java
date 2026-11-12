package com.curso.diccionario.controlador.rest;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.controlador.rest.api.ApiRestDiccionarios;
import com.curso.diccionario.controlador.rest.api.DiccionarioDTO;
import com.curso.diccionario.controlador.rest.api.PalabraDTO;
import com.curso.diccionario.controlador.rest.api.SignificadoDTO;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.api.BuscadorDePalabrasException;
import com.curso.diccionario.service.api.IdiomaNoSoportado;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.api.PalabraNoEncontrada;
import com.curso.diccionario.service.api.ResultadoDeBusqueda;

// Un CONTROLADOR: solo EXPONE el servicio por un protocolo, aquí HTTP/REST. No tiene lógica de
// negocio (esa es el BuscadorDePalabras, el mismo que usa la app de consola): traduce peticiones
// HTTP a llamadas al servicio, y sus respuestas a status y JSON. Las URLs, los parámetros y la
// documentación vienen del interfaz; los errores se lanzan como excepción y los convierte en
// respuesta GestorDeErroresRest, para no mezclar códigos HTTP con nada más.
@RestController
public class ControladorRestDiccionarios implements ApiRestDiccionarios {

    private final BuscadorDePalabras buscador;

    // Solo conoce el servicio: ni qué diccionarios hay detrás ni dónde se guardan.
    public ControladorRestDiccionarios(BuscadorDePalabras buscador) {
        this.buscador = buscador;
    }

    @Override
    public List<String> listarIdiomas() {
        try {
            return buscador.idiomasDisponibles();
        } catch (BuscadorDePalabrasException e) {
            // Un fallo llega al cliente como 500, no como una lista vacía que parezca "no hay idiomas".
            throw new ErrorAlConsultarDiccionarioException("Error al obtener los idiomas", e);
        }
    }

    @Override
    public DiccionarioDTO obtenerDiccionario(String idioma) {
        boolean soportado;
        try {
            soportado = buscador.idiomaSoportado(idioma);
        } catch (BuscadorDePalabrasException e) {
            throw new ErrorAlConsultarDiccionarioException("Error al comprobar el diccionario de " + idioma, e);
        }
        if (!soportado) {
            throw new IdiomaNoDisponibleException(idioma);
        }
        return new DiccionarioDTO(idioma);
    }

    @Override
    public PalabraDTO buscarPalabra(String idioma, String palabra, boolean ignorarMayusculasYAcentos) {
        ResultadoDeBusqueda resultado;
        try {
            resultado = ignorarMayusculasYAcentos
                    ? buscador.buscarIgnorandoMayusculasYAcentos(palabra, idioma)
                    : buscador.buscarExacta(palabra, idioma);
        } catch (BuscadorDePalabrasException e) {
            throw new ErrorAlConsultarDiccionarioException("Error al buscar " + palabra + " en " + idioma, e);
        }
        // Java 14: switch como expresión: devuelve un valor, con case ... -> y sin break.
        // Java 21: switch con patrones sobre el resultado sellado del servicio. Exhaustivo: cada
        // uno de los tres casos tiene su respuesta HTTP, y si el servicio añade un cuarto, esto deja
        // de compilar en vez de responder cualquier cosa.
        return switch (resultado) {
            case PalabraEncontrada(String enElDiccionario, String i, List<Significado> significados) ->
                    // Java 8: referencia a método (::), una lambda que solo llama a un método.
                    // Java 8: streams, el map-reduce de Java (stream, map, filter, collect...).
                    // Java 16: Stream.toList(), lista inmutable sin Collectors.toList().
                    new PalabraDTO(i, enElDiccionario, significados.stream().map(this::aDTO).toList());
            // 1.1.0: las sugerencias del servicio viajan en el cuerpo del 404.
            case PalabraNoEncontrada noEncontrada ->
                    throw new PalabraNoEncontradaException(noEncontrada.idioma(), noEncontrada.palabra(), noEncontrada.sugerencias());
            case IdiomaNoSoportado noSoportado ->
                    throw new IdiomaNoDisponibleException(noSoportado.idioma());
        };
    }

    private SignificadoDTO aDTO(Significado significado) {
        // El contrato REST promete lista vacía, nunca null; el API Java no lo promete.
        // Java 9: List.of / Map.of / Set.of, colecciones inmutables en una línea.
        List<String> ejemplos = significado.getEjemplos() == null ? List.of() : significado.getEjemplos();
        return new SignificadoDTO(significado.getTexto(), ejemplos);
    }
}
