package com.curso.diccionario.impl.bbdd;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.impl.bbdd.modelo.DiccionarioEntidad;
import com.curso.diccionario.impl.bbdd.modelo.PalabraEntidad;
import com.curso.diccionario.impl.bbdd.modelo.RepositorioDeDiccionarios;
import com.curso.diccionario.impl.bbdd.modelo.RepositorioDePalabras;
import com.curso.diccionario.impl.bbdd.modelo.SignificadoEntidad;

// Todo el acceso a la BBDD pasa por aquí, cada operación en su transacción.
// Por defecto de solo lectura; las que escriben lo dicen.
// Público: lo usa también el cargador-de-bbdd-palabras para escribir los diccionarios.
@Transactional(readOnly = true)
public class AlmacenDeDiccionarios {

    private final RepositorioDeDiccionarios diccionarios;
    private final RepositorioDePalabras palabras;

    public AlmacenDeDiccionarios(RepositorioDeDiccionarios diccionarios, RepositorioDePalabras palabras) {
        this.diccionarios = diccionarios;
        this.palabras = palabras;
    }

    // SELECT idioma FROM diccionarios
    public List<String> idiomas() {
        // Java 8: referencia a método (::), una lambda que solo llama a un método.
        // Java 8: streams, el map-reduce de Java (stream, map, filter, collect...).
        // Java 16: Stream.toList(), lista inmutable sin Collectors.toList().
        return diccionarios.findAll().stream().map(DiccionarioEntidad::getIdioma).sorted().toList();
    }

    // SELECT * FROM diccionarios WHERE idioma = ?
    public boolean tieneIdioma(String idioma) {
        return diccionarios.existsById(idioma);
    }

    public boolean existe(String idioma, String palabra) {
        return palabras.existsByIdiomaAndTexto(idioma, palabra);
    }

    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    public Optional<List<Significado>> significados(String idioma, String palabra) {
        // Se copian dentro de la transacción, mientras las colecciones perezosas aún se pueden leer.
        return palabras.findByIdiomaAndTexto(idioma, palabra)
                // Java 8: lambda, una función anónima escrita dentro de una expresión.
                .map(encontrada -> encontrada.getSignificados().stream()
                        .<Significado>map(significado -> new SignificadoEnBBDD(significado.getTexto(), significado.getEjemplos()))
                        // Java 16: Stream.toList(), lista inmutable sin Collectors.toList().
                        .toList());
    }

    // SELECT texto FROM palabras WHERE idioma = ?
    public List<String> palabras(String idioma) {
        return palabras.textosDe(idioma);
    }

    // Todo en una transacción: nadie ve el idioma a medio cargar, y si algo falla queda el de antes.
    @Transactional
    public void recargar(String idioma, Map<String, List<Significado>> palabrasConSignificados) {
        borrarPalabrasDe(idioma);
        diccionarios.save(new DiccionarioEntidad(idioma));
        palabras.saveAll(palabrasConSignificados.entrySet().stream()
                .map(entrada -> new PalabraEntidad(idioma, entrada.getKey(), entrada.getValue().stream()
                        .map(significado -> new SignificadoEntidad(significado.getTexto(), significado.getEjemplos()))
                        .toList()))
                .toList());
    }

    @Transactional
    public void borrar(String idioma) {
        borrarPalabrasDe(idioma);
        diccionarios.deleteById(idioma);
    }

    private void borrarPalabrasDe(String idioma) {
        palabras.borrarEjemplosDe(idioma);
        palabras.borrarSignificadosDe(idioma);
        palabras.borrarPalabrasDe(idioma);
    }
}
