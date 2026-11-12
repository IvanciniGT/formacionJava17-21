package com.curso.diccionario.service.rest;

import java.util.List;

import com.curso.diccionario.api.Significado;

// Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
record SignificadoRemoto(String getTexto, List<String> getEjemplos) implements Significado {

    SignificadoRemoto {
        // El contrato REST promete lista vacía, pero un JSON sin el campo llega como null.
        // Java 9: List.of / Map.of / Set.of, colecciones inmutables en una línea.
        // Java 10: List.copyOf / Map.copyOf, copia inmutable (y si ya lo era, no copia nada).
        getEjemplos = getEjemplos == null ? List.of() : List.copyOf(getEjemplos);
    }
}
