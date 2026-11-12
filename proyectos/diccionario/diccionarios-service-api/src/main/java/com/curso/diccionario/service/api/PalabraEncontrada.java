package com.curso.diccionario.service.api;

import java.util.List;

import com.curso.diccionario.api.Significado;

import lombok.NonNull;

// Cada resultado lleva EXACTAMENTE lo que tiene sentido en su caso, y nada más (Segregación de
// Interfaces): no hay un registro con "palabraExiste", "idiomaSoportado" y unos significados que
// solo valen según lo que digan los otros dos campos.
//
// La palabra, como está escrita en el diccionario ("atún", aunque se buscase "Atun"); el idioma,
// su código ("ES").
//
// Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
public record PalabraEncontrada(@NonNull String palabra, @NonNull String idioma, @NonNull List<Significado> significados)
        implements ResultadoDeBusqueda {

    // Constructor compacto: valida y normaliza antes de que se asignen los campos.
    public PalabraEncontrada {
        if (significados.isEmpty()) {
            throw new IllegalArgumentException("Una palabra encontrada tiene al menos un significado");
        }
        // Java 10: List.copyOf / Map.copyOf, copia inmutable (y si ya lo era, no copia nada).
        significados = List.copyOf(significados);
    }
}
