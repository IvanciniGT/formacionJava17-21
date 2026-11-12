package com.curso.diccionario.app.consola.factorias;

import java.util.ServiceLoader;

import com.curso.diccionario.service.api.BuscadorDePalabras;

// FACTORÍA: el único sitio que sabe de dónde sale el servicio de búsqueda. La aplicación le pide
// uno y no sabe cuál le dan. En el servidor, esto mismo lo hace Spring con un @Bean.
//
// No hace el new de una clase concreta: se lo pide al ServiceLoader, que da el que haya en el
// module path. Así, cambiar de servicio (el local o el que habla con el servidor) no es cambiar
// esta línea: es cambiar qué jar lleva el ensamblado (diccionarios-app-consola-ficheros,
// diccionarios-app-consola-servidor...).
public final class BuscadorDePalabrasFactory {

    private BuscadorDePalabrasFactory() {}

    public static BuscadorDePalabras getInstance() {
        return ServiceLoader.load(BuscadorDePalabras.class)
                .findFirst()
                // Java 8: lambda, una función anónima escrita dentro de una expresión.
                .orElseThrow(() -> new IllegalStateException("No hay ningún BuscadorDePalabras en el module path"));
    }
}
