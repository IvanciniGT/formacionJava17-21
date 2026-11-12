package com.curso.diccionario.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.api.contrato.SignificadoDePrueba;

// Lo más tonto que hace de repositorio, para probar el servicio sin ficheros ni BBDD.
@SuppressWarnings("removal")
class SuministradorEnMemoria implements SuministradorDeDiccionarios {

    private final Map<String, Map<String, List<SignificadoDePrueba>>> diccionarios;

    SuministradorEnMemoria(DatosDePrueba datos) {
        this.diccionarios = datos.getDiccionarios();
    }

    @Override public List<String> getIdiomas() { return List.copyOf(diccionarios.keySet()); }
    @Override public boolean tienesDiccionarioDe(String idioma) { return diccionarios.containsKey(idioma); }
    @Override public Optional<Diccionario> getDiccionario(String idioma) {
        return Optional.ofNullable(diccionarios.get(idioma)).map(palabras -> new Diccionario() {
            @Override public String getIdioma() { return idioma; }
            @Override public boolean existe(String palabra) { return palabras.containsKey(palabra); }
            @Override public Optional<List<Significado>> getSignificados(String palabra) {
                return Optional.ofNullable(palabras.get(palabra))
                        .map(significados -> significados.stream().<Significado>map(SignificadoEnMemoria::new).toList());
            }
            @Override public List<String> getPalabras() { return List.copyOf(palabras.keySet()); }
        });
    }

    // Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
    record SignificadoEnMemoria(SignificadoDePrueba datos) implements Significado {
        @Override public String getTexto() { return datos.texto(); }
        @Override public List<String> getEjemplos() { return datos.ejemplos(); }
    }
}
