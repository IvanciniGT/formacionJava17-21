package com.curso.diccionario.cargador.modelo;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Qué fichero se cargó para cada idioma: si al arrancar el fichero tiene la misma huella, no hay
// nada que recargar. Es cosa del cargador, no del repositorio: por eso vive en este módulo.
@Entity
@Table(name = "huellas")
public class HuellaDeFichero {

    @Id
    @Column(length = 10)
    private String idioma;

    // SHA-256 en hexadecimal: si no cambia, el fichero es el mismo y no hay nada que recargar.
    @Column(nullable = false, length = 64)
    private String sha256;

    @Column(nullable = false)
    private Instant cargadoEn;

    protected HuellaDeFichero() {
    }

    public HuellaDeFichero(String idioma, String sha256, Instant cargadoEn) {
        this.idioma = idioma;
        this.sha256 = sha256;
        this.cargadoEn = cargadoEn;
    }

    public String getIdioma() {
        return idioma;
    }

    public String getSha256() {
        return sha256;
    }
}
