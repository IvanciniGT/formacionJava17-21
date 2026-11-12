package com.curso.diccionario.impl.ficheros;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;

import lombok.Getter;
import lombok.NonNull;

// Igual que en la 1.0.0 del API: leer de un Map en memoria no puede fallar, así que los métodos
// nuevos que avisan de errores (existeLaPalabra, buscarSignificados, listarPalabras) le sirven
// tal cual vienen, con su default. Ni una línea cambiada por la 1.1.0.
@SuppressWarnings("removal")
public class DiccionarioDesdeFichero implements Diccionario {
    @Getter
    private final String idioma;
    private final Map<String, List<Significado>> palabrasYSignificados;
    // Un diccionario tendrá todas sus palabras + significados.

    public DiccionarioDesdeFichero(String idioma, Map<String, List<Significado>> palabrasYSignificados) {
        this.idioma = idioma;
        this.palabrasYSignificados = palabrasYSignificados;
    }

    @Override
    public boolean existe(@NonNull String palabra) {
        return palabrasYSignificados.containsKey(palabra);
    }

    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    @Override
    public Optional<List<Significado>> getSignificados(@NonNull String palabra) {
        // ofNullable: get devuelve null si la palabra no está, y null es justo lo que Optional evita.
        return Optional.ofNullable(palabrasYSignificados.get(palabra));
    }

    @Override
    public List<String> getPalabras() {
        // Java 10: List.copyOf / Map.copyOf, copia inmutable (y si ya lo era, no copia nada).
        return List.copyOf(palabrasYSignificados.keySet());
    }

}
