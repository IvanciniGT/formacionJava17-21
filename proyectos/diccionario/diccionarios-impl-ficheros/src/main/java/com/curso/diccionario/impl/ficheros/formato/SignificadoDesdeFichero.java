package com.curso.diccionario.impl.ficheros.formato;

import java.util.List;

import com.curso.diccionario.api.Significado;

// Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
public record SignificadoDesdeFichero(String texto, List<String> ejemplos) implements Significado {

    // El record genera texto() y ejemplos(); la interfaz pide los nombres con get.
    @Override
    public String getTexto() {
        return texto;
    }

    @Override
    public List<String> getEjemplos() {
        return ejemplos;
    }

}
