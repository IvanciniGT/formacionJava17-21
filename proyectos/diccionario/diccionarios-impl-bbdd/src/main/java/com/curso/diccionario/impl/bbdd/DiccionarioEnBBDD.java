package com.curso.diccionario.impl.bbdd;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;

// No guarda palabras: cada pregunta va a la BBDD, que es quien tiene los índices.
// Un repositorio de bajo nivel: cada método es, casi tal cual, un SELECT.
@SuppressWarnings("removal")
class DiccionarioEnBBDD implements Diccionario {

    private final String idioma;
    private final AlmacenDeDiccionarios almacen;

    DiccionarioEnBBDD(String idioma, AlmacenDeDiccionarios almacen) {
        this.idioma = idioma;
        this.almacen = almacen;
    }

    @Override
    public String getIdioma() {
        return idioma;
    }

    // Los métodos nuevos (API 1.1.0) dejan salir los errores de la BBDD: los de Spring Data no son
    // comprobados, y el servicio los recoge como cualquier otro fallo. Los viejos, que no pueden
    // avisar, los disfrazan como pueden, y por eso se eliminarán.

    @Override
    public boolean existeLaPalabra(String palabra) {
        return almacen.existe(idioma, Objects.requireNonNull(palabra, "palabra"));
    }

    // Se eliminará
    @Override
    public boolean existe(String palabra) {
        Objects.requireNonNull(palabra, "palabra");
        try {
            return existeLaPalabra(palabra);
        } catch (RuntimeException e) {
            return false;
        }
    }

    // SELECT ... FROM significados JOIN palabras ... WHERE idioma = ? AND texto = ?
    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    @Override
    public Optional<List<Significado>> buscarSignificados(String palabra) {
        return almacen.significados(idioma, Objects.requireNonNull(palabra, "palabra"));
    }

    // Se eliminará
    @Override
    public Optional<List<Significado>> getSignificados(String palabra) {
        Objects.requireNonNull(palabra, "palabra");
        try {
            return buscarSignificados(palabra);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    // SELECT texto FROM palabras WHERE idioma = ?
    @Override
    public List<String> listarPalabras() {
        return almacen.palabras(idioma);
    }

    // Se eliminará
    @Override
    public List<String> getPalabras() {
        try {
            return listarPalabras();
        } catch (RuntimeException e) {
            // Java 9: List.of / Map.of / Set.of, colecciones inmutables en una línea.
            return List.of();
        }
    }
}
