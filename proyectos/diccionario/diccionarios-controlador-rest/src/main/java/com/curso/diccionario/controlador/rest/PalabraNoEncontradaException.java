package com.curso.diccionario.controlador.rest;

import java.util.List;

public class PalabraNoEncontradaException extends RuntimeException {

    private final List<String> sugerencias;

    public PalabraNoEncontradaException(String idioma, String palabra, List<String> sugerencias) {
        super("La palabra " + palabra + " no existe en el idioma " + idioma);
        // Java 10: List.copyOf / Map.copyOf, copia inmutable (y si ya lo era, no copia nada).
        this.sugerencias = List.copyOf(sugerencias);
    }

    public List<String> getSugerencias() {
        return sugerencias;
    }
}
