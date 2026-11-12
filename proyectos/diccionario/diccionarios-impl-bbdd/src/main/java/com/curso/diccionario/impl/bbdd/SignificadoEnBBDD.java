package com.curso.diccionario.impl.bbdd;

import java.util.List;

import com.curso.diccionario.api.Significado;

// Copia sin JPA de lo leído: lo que sale del almacén no puede ser una entidad, porque fuera de la
// transacción sus colecciones perezosas fallarían al recorrerlas.
// Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
record SignificadoEnBBDD(String texto, List<String> ejemplos) implements Significado {

    SignificadoEnBBDD {
        // Java 10: List.copyOf / Map.copyOf, copia inmutable (y si ya lo era, no copia nada).
        ejemplos = List.copyOf(ejemplos);
    }

    @Override
    public String getTexto() {
        return texto;
    }

    @Override
    public List<String> getEjemplos() {
        return ejemplos;
    }
}
