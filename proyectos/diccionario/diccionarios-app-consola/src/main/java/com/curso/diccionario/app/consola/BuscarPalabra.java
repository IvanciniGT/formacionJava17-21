package com.curso.diccionario.app.consola;

import com.curso.diccionario.app.consola.factorias.BuscadorDePalabrasFactory;
import com.curso.diccionario.app.consola.factorias.InterfazDeUsuarioFactory;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// Sin lógica: pide las piezas a las factorías y se las pasa al procesador.
public class BuscarPalabra {

    public static void main(String[] args) {
        // Los componentes concretos que va a usar la aplicación: los que den las factorías.
        InterfazDeUsuario interfaz = InterfazDeUsuarioFactory.getInstance(args);
        BuscadorDePalabras buscador = BuscadorDePalabrasFactory.getInstance();

        // Y en marcha, para que procese la petición del usuario.
        System.exit(new ProcesadorDePeticiones(interfaz, buscador).procesarPeticion());
    }

}
