package com.curso.diccionario.service.api;

import java.util.List;

import lombok.NonNull;

// El SERVICIO de búsqueda: LÓGICA DE NEGOCIO. Lo que el sistema sabe hacer, sin decir cómo se le
// pide: quien lo expone por un protocolo es otro componente. La app de consola lo expone por la
// línea de comandos; el controlador REST, por HTTP. Podría haber uno SOAP, y los dos expondrían
// este mismo servicio.
//
// No es el API de diccionarios: ese es un REPOSITORIO (lógica de persistencia), con funciones de
// muy bajo nivel, casi un SELECT. Este no es solo un agregado de aquellas: aporta lógica de negocio
// (qué se considera la misma palabra, qué resultado es cada caso).
public interface BuscadorDePalabras {

    List<String> idiomasDisponibles() throws BuscadorDePalabrasException;

    // El idioma es un código: "es", "Es" y "ES" son el mismo en todas las operaciones.
    boolean idiomaSoportado(@NonNull String idioma) throws BuscadorDePalabrasException;

    // Tres resultados posibles, y no un Optional: con un Optional vacío quien pregunta no sabe si la
    // palabra no existe o si el idioma no está soportado. Los tres son caminos normales, no
    // excepcionales: la excepción queda solo para cuando algo falla de verdad.
    //
    // La palabra, tal cual: "MELÓN" no es "melón", ni "melon".
    ResultadoDeBusqueda buscarExacta(@NonNull String palabra, @NonNull String idioma) throws BuscadorDePalabrasException;

    // "MEMBRILLO" encuentra "membrillo", y "Atun", "atún". Lo que no se toca es la ñ: "ano" no es "año".
    // Si la encuentra, el resultado dice cómo está escrita en el diccionario.
    ResultadoDeBusqueda buscarIgnorandoMayusculasYAcentos(@NonNull String palabra, @NonNull String idioma)
            throws BuscadorDePalabrasException;

}
