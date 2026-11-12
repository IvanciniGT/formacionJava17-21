package com.curso.diccionario.cargador;

import java.io.IOException;
import java.util.List;

import com.curso.diccionario.impl.ficheros.formato.CatalogoDeDiccionariosEnClasspath;
import com.curso.diccionario.impl.ficheros.formato.FormatoDeFicheroDeDiccionario;

class FuenteDeDiccionariosEnClasspath implements FuenteDeDiccionarios {

    private final CatalogoDeDiccionariosEnClasspath catalogo = new CatalogoDeDiccionariosEnClasspath(getClass().getClassLoader());

    @Override
    public List<String> idiomas() throws IOException {
        return catalogo.idiomas();
    }

    @Override
    public byte[] contenido(String idioma) throws IOException {
        return FormatoDeFicheroDeDiccionario.leerBytes(catalogo.fichero(idioma)
                // Java 8: lambda, una función anónima escrita dentro de una expresión.
                .orElseThrow(() -> new IOException("No hay fichero de diccionario para " + idioma)));
    }
}
