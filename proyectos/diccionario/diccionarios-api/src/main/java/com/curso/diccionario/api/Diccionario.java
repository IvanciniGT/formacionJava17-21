package com.curso.diccionario.api;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;

public interface Diccionario {

    String getIdioma();

    boolean existe(@NonNull String palabra);

    // Vacío si la palabra no existe; si existe, al menos un significado.
    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    Optional<List<Significado>> getSignificados(@NonNull String palabra);

    // Todas las palabras del diccionario, tal cual están escritas (con sus acentos).
    // Un repositorio de bajo nivel: es casi un SELECT palabra FROM palabras WHERE idioma = ?.
    // Lo que se haga con ellas (buscar sin acentos, sugerir parecidas) es lógica de negocio:
    // eso es cosa del servicio, no de aquí.
    List<String> getPalabras();

}
