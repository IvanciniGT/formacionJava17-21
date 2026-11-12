package com.curso.diccionario.service.rest;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.controlador.rest.api.CodigoDeError;
import com.curso.diccionario.controlador.rest.api.PalabraDTO;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.api.BuscadorDePalabrasException;
import com.curso.diccionario.service.api.IdiomaNoSoportado;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.api.PalabraNoEncontrada;
import com.curso.diccionario.service.api.ResultadoDeBusqueda;
import com.google.gson.reflect.TypeToken;

import lombok.NonNull;

// El SERVICIO de búsqueda... que no busca nada: se lo pregunta al servidor central, donde está el
// de verdad (BuscadorDePalabrasEnDiccionarios) con los diccionarios. La lógica de negocio solo
// existe allí: si cambia, cambia para todos sin reinstalar nada aquí.
//
// Para la app de consola es el mismo interfaz: con este jar o con el local en el module path,
// la consola no cambia ni una línea (se elige con un perfil de Maven).
public class BuscadorDePalabrasRemoto implements BuscadorDePalabras {

    static final String VARIABLE_DE_ENTORNO_SERVIDOR = "DICCIONARIOS_SERVIDOR";
    static final String SERVIDOR_POR_DEFECTO = "http://localhost:8080";

    private static final Type LISTA_DE_TEXTOS = new TypeToken<List<String>>() {}.getType();

    private final ClienteDelServidorDeDiccionarios cliente;

    public BuscadorDePalabrasRemoto(String servidor) {
        this.cliente = new ClienteDelServidorDeDiccionarios(servidor);
    }

    // El ServiceLoader solo sabe instanciar con un constructor sin argumentos o con este método;
    // así el constructor puede seguir pidiendo el servidor, que es lo que usan las pruebas.
    public static BuscadorDePalabras provider() {
        String servidorDeclarado = System.getenv(VARIABLE_DE_ENTORNO_SERVIDOR);
        return new BuscadorDePalabrasRemoto(servidorDeclarado != null ? servidorDeclarado : SERVIDOR_POR_DEFECTO);
    }

    @Override
    public List<String> idiomasDisponibles() throws BuscadorDePalabrasException {
        HttpResponse<String> respuesta = cliente.get(cliente.rutaDeDiccionarios());
        if (respuesta.statusCode() != 200) {
            throw cliente.respuestaInesperada(respuesta);
        }
        List<String> idiomas = cliente.leer(respuesta, LISTA_DE_TEXTOS);
        // Java 9: List.of / Map.of / Set.of, colecciones inmutables en una línea.
        // Java 10: List.copyOf / Map.copyOf, copia inmutable (y si ya lo era, no copia nada).
        return idiomas == null ? List.of() : List.copyOf(idiomas);
    }

    @Override
    public boolean idiomaSoportado(@NonNull String idioma) throws BuscadorDePalabrasException {
        HttpResponse<String> respuesta = cliente.get(cliente.rutaDeDiccionario(idioma));
        if (respuesta.statusCode() == 200) {
            return true;
        }
        if (cliente.es404(respuesta, CodigoDeError.IDIOMA_NO_DISPONIBLE)) {
            return false;
        }
        throw cliente.respuestaInesperada(respuesta);
    }

    @Override
    public ResultadoDeBusqueda buscarExacta(@NonNull String palabra, @NonNull String idioma) throws BuscadorDePalabrasException {
        return buscar(palabra, idioma, false);
    }

    @Override
    public ResultadoDeBusqueda buscarIgnorandoMayusculasYAcentos(@NonNull String palabra, @NonNull String idioma)
            throws BuscadorDePalabrasException {
        return buscar(palabra, idioma, true);
    }

    // Cada respuesta HTTP vuelve a ser uno de los tres resultados del servicio: 200, los dos 404
    // (que solo distingue el código del cuerpo), y cualquier otra cosa es un error de verdad.
    private ResultadoDeBusqueda buscar(String palabra, String idioma, boolean ignorarMayusculasYAcentos)
            throws BuscadorDePalabrasException {
        HttpResponse<String> respuesta = cliente.get(cliente.rutaDePalabra(idioma, palabra, ignorarMayusculasYAcentos));
        if (respuesta.statusCode() == 200) {
            PalabraDTO dto = cliente.leer(respuesta, PalabraDTO.class);
            // La palabra y el idioma de la respuesta: como están en el diccionario ("atún", "ES").
            // Java 8: streams, el map-reduce de Java (stream, map, filter, collect...).
            return new PalabraEncontrada(dto.palabra(), dto.idioma(), dto.significados().stream()
                    // <Significado>: sin él, toList() da List<SignificadoRemoto>.
                    // Java 8: lambda, una función anónima escrita dentro de una expresión.
                    .<Significado>map(significado -> new SignificadoRemoto(significado.texto(), significado.ejemplos()))
                    // Java 16: Stream.toList(), lista inmutable sin Collectors.toList().
                    .toList());
        }
        if (cliente.es404(respuesta, CodigoDeError.PALABRA_NO_ENCONTRADA)) {
            return new PalabraNoEncontrada(palabra, idioma);
        }
        if (cliente.es404(respuesta, CodigoDeError.IDIOMA_NO_DISPONIBLE)) {
            return new IdiomaNoSoportado(palabra, idioma);
        }
        throw cliente.respuestaInesperada(respuesta);
    }
}
