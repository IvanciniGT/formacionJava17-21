package com.curso.diccionario.api;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;

public interface SuministradorDeDiccionarios {

    List<String> getIdiomas();

    // Existe aparte de getDiccionario porque una implementación puede saber qué idiomas
    // tiene sin llegar a cargar el diccionario.
    boolean tienesDiccionarioDe(@NonNull String idioma);

    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    Optional<Diccionario> getDiccionario(@NonNull String idioma);

}
