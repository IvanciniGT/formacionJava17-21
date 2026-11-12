package com.curso.diccionario.service.api;

import lombok.NonNull;

// La palabra y el idioma, tal y como se preguntaron.
//
// Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
public record IdiomaNoSoportado(@NonNull String palabra, @NonNull String idioma) implements ResultadoDeBusqueda {
}
