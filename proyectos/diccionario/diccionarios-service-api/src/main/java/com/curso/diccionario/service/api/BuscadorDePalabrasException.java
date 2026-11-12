package com.curso.diccionario.service.api;

// Solo para lo que no es un camino normal: el disco no se puede leer, el servidor no contesta...
// "La palabra no existe" no es un error: es uno de los tres resultados.
public class BuscadorDePalabrasException extends Exception {

    public BuscadorDePalabrasException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
