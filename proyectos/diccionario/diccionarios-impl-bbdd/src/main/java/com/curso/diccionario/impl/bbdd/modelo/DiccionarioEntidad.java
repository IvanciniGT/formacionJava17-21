package com.curso.diccionario.impl.bbdd.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Un idioma con diccionario. Que haya fila es lo que hace que el idioma exista, aunque su
// diccionario no tenga palabras (SELECT * FROM diccionarios WHERE idioma = ?).
@Entity
@Table(name = "diccionarios")
public class DiccionarioEntidad {

    @Id
    @Column(length = 10)
    private String idioma;

    protected DiccionarioEntidad() {
    }

    public DiccionarioEntidad(String idioma) {
        this.idioma = idioma;
    }

    public String getIdioma() {
        return idioma;
    }
}
