package com.curso.diccionario.service.impl;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceLoader;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.api.BuscadorDePalabrasException;
import com.curso.diccionario.service.api.IdiomaNoSoportado;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.api.PalabraNoEncontrada;
import com.curso.diccionario.service.api.ResultadoDeBusqueda;

import lombok.NonNull;

// La lógica de negocio de la búsqueda: qué se considera la misma palabra, qué resultado es cada caso.
// Los diccionarios (el repositorio) solo guardan y devuelven; quien decide es esto.
public class BuscadorDePalabrasEnDiccionarios implements BuscadorDePalabras {

    private final SuministradorDeDiccionarios suministrador;

    // El que usa el servidor y las pruebas: le dan el suministrador hecho.
    public BuscadorDePalabrasEnDiccionarios(SuministradorDeDiccionarios suministrador) {
        this.suministrador = suministrador;
    }

    // El que usa el ServiceLoader en la app de consola: el suministrador, el que haya en el module path.
    public static BuscadorDePalabras provider() {
        return new BuscadorDePalabrasEnDiccionarios(ServiceLoader.load(SuministradorDeDiccionarios.class)
                .findFirst()
                // Java 8: lambda, una función anónima escrita dentro de una expresión.
                .orElseThrow(() -> new IllegalStateException("No hay ningún SuministradorDeDiccionarios en el module path")));
    }

    // En la 1.0.0 del API de diccionarios no hay forma de que un fallo llegue hasta aquí: la
    // excepción del servicio está en la firma (el contrato ya la prevé), pero esta versión no
    // tiene de dónde sacarla.

    @Override
    public List<String> idiomasDisponibles() {
        // Sin versión que avise: si falla, getIdiomas solo puede devolver una lista vacía.
        return suministrador.getIdiomas();
    }

    @Override
    public boolean idiomaSoportado(@NonNull String idioma) {
        return suministrador.tienesDiccionarioDe(normalizarIdioma(idioma));
    }

    @Override
    public ResultadoDeBusqueda buscarExacta(@NonNull String palabra, @NonNull String idioma) {
        // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
        Optional<Diccionario> diccionario = diccionarioDe(normalizarIdioma(idioma));
        if (diccionario.isEmpty()) {
            return new IdiomaNoSoportado(palabra, idioma);
        }
        return encontradaONo(diccionario.get(), palabra, palabra, idioma);
    }

    @Override
    public ResultadoDeBusqueda buscarIgnorandoMayusculasYAcentos(@NonNull String palabra, @NonNull String idioma) {
        Optional<Diccionario> diccionario = diccionarioDe(normalizarIdioma(idioma));
        if (diccionario.isEmpty()) {
            return new IdiomaNoSoportado(palabra, idioma);
        }
        // Lo barato primero: si está tal cual, no hace falta recorrer el diccionario.
        if (significadosDe(diccionario.get(), palabra).isPresent()) {
            return encontradaONo(diccionario.get(), palabra, palabra, idioma);
        }
        String buscada = normalizarPalabra(palabra);
        // Si varias se escriben igual sin acentos ("papa" y "papá"), la primera en orden alfabético.
        // Java 8: streams, el map-reduce de Java (stream, map, filter, collect...).
        Optional<String> enElDiccionario = palabrasDe(diccionario.get()).stream()
                .filter(candidata -> normalizarPalabra(candidata).equals(buscada))
                .sorted()
                .findFirst();
        return encontradaONo(diccionario.get(), enElDiccionario.orElse(palabra), palabra, idioma);
    }

    private ResultadoDeBusqueda encontradaONo(Diccionario diccionario, String enElDiccionario, String palabra, String idioma) {
        Optional<List<Significado>> significados = significadosDe(diccionario, enElDiccionario);
        if (significados.isEmpty()) {
            return new PalabraNoEncontrada(palabra, idioma);
        }
        return new PalabraEncontrada(enElDiccionario, diccionario.getIdioma(), significados.get());
    }

    private Optional<Diccionario> diccionarioDe(String idioma) {
        return suministrador.getDiccionario(idioma);
    }

    private static Optional<List<Significado>> significadosDe(Diccionario diccionario, String palabra) {
        return diccionario.getSignificados(palabra);
    }

    private static List<String> palabrasDe(Diccionario diccionario) {
        return diccionario.getPalabras();
    }

    // El idioma es un código, no una palabra: "es", "Es" y "ES" son el mismo, en todas las búsquedas.
    // Java 11: métodos nuevos de String (isBlank, strip, lines, repeat...). strip y no trim:
    // trim solo quita los espacios ASCII; strip, cualquier espacio Unicode.
    // Locale.ROOT: con el locale turco, "i".toUpperCase() no da "I".
    static String normalizarIdioma(String idioma) {
        return idioma.strip().toUpperCase(Locale.ROOT);
    }

    // "MEMBRILLO" -> "membrillo", "Atún" -> "atun". Pero la ñ se queda: "año" no es "ano".
    // NFD separa cada letra de su acento ("á" pasa a ser "a" + U+0301); se quitan los acentos
    // (U+0300 a U+036F) salvo la virgulilla (U+0303), y NFC vuelve a juntar "n" + U+0303 en "ñ".
    static String normalizarPalabra(String palabra) {
        String separada = Normalizer.normalize(palabra.strip().toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return Normalizer.normalize(separada.replaceAll("[\\u0300-\\u0302\\u0304-\\u036f]", ""), Normalizer.Form.NFC);
    }
}
