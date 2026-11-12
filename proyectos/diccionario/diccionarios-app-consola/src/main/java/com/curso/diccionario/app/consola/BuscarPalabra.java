package com.curso.diccionario.app.consola;

import java.util.List;
import java.util.ServiceLoader;

import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.ui.consola.api.FabricaDeInterfacesDeUsuario;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// Sin lógica: solo monta las piezas y se las pasa al procesador.
public class BuscarPalabra {

    public static void main(String[] args) {
        // El único sitio que decide qué implementaciones se usan, y ni siquiera las nombra:
        // las que haya en el module path.
        // Java 9: List.of / Map.of / Set.of, colecciones inmutables en una línea.
        InterfazDeUsuario interfaz = cargar(FabricaDeInterfacesDeUsuario.class).crear(List.of(args));
        BuscadorDePalabras buscador = cargar(BuscadorDePalabras.class);

        System.exit(new ProcesadorDePeticiones(interfaz, buscador).procesarPeticion());
    }

    private static <T> T cargar(Class<T> tipo) {
        return ServiceLoader.load(tipo)
                .findFirst()
                // Java 8: lambda, una función anónima escrita dentro de una expresión.
                .orElseThrow(() -> new IllegalStateException("No hay ninguna implementación de " + tipo.getSimpleName() + " en el module path"));
    }

}
