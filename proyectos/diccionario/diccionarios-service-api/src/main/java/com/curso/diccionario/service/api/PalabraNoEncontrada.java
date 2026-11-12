package com.curso.diccionario.service.api;

import java.util.List;

import lombok.NonNull;

// La palabra y el idioma, tal y como se preguntaron; y desde la 1.1.0, las palabras del diccionario
// que se le parecen, de la más a la menos parecida (puede venir vacía).
//
// 1.1.0, un MINOR, con cuidado de no romper a nadie: solo crece esta clase de transporte, el
// servicio no gana ningún método. El constructor de antes sigue existiendo, así que quien creaba
// resultados con (palabra, idioma) sigue compilando, y un switch con patrón de tipo
// (case PalabraNoEncontrada noEncontrada) también. Lo único que no aguanta es un record pattern
// con dos campos (case PalabraNoEncontrada(String p, String i)): ese hay que cambiarlo. Es el
// coste de evolucionar un record que está en el permits de un sealed.
//
// Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
public record PalabraNoEncontrada(@NonNull String palabra, @NonNull String idioma, @NonNull List<String> sugerencias)
        implements ResultadoDeBusqueda {

    public PalabraNoEncontrada {
        // Java 10: List.copyOf / Map.copyOf, copia inmutable (y si ya lo era, no copia nada).
        sugerencias = List.copyOf(sugerencias);
    }

    // El de la 1.0.0: sin sugerencias.
    public PalabraNoEncontrada(@NonNull String palabra, @NonNull String idioma) {
        // Java 9: List.of / Map.of / Set.of, colecciones inmutables en una línea.
        this(palabra, idioma, List.of());
    }
}
