package com.curso.diccionario.app.consola.factorias;

import java.util.List;
import java.util.ServiceLoader;

import com.curso.diccionario.ui.consola.api.FabricaDeInterfacesDeUsuario;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// FACTORÍA de la interfaz de usuario. La implementación también la pone el ServiceLoader: la
// que lleve el ensamblado. Como necesita los argumentos del programa, lo que se carga no es la
// interfaz sino una fábrica que la crea con ellos.
public final class InterfazDeUsuarioFactory {

    private InterfazDeUsuarioFactory() {}

    public static InterfazDeUsuario getInstance(String[] argumentos) {
        return ServiceLoader.load(FabricaDeInterfacesDeUsuario.class)
                .findFirst()
                // Java 8: lambda, una función anónima escrita dentro de una expresión.
                .orElseThrow(() -> new IllegalStateException("No hay ninguna interfaz de usuario en el module path"))
                // Java 9: List.of / Map.of / Set.of, colecciones inmutables en una línea.
                .crear(List.of(argumentos));
    }
}
