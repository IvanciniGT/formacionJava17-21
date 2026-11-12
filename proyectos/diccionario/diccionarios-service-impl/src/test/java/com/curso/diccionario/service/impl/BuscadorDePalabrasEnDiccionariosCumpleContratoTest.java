package com.curso.diccionario.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.api.BuscadorDePalabrasException;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.contrato.ContratoBuscadorDePalabrasTest;

class BuscadorDePalabrasEnDiccionariosCumpleContratoTest extends ContratoBuscadorDePalabrasTest {

    @Override
    protected BuscadorDePalabras crearBuscadorCon(DatosDePrueba datos) {
        return new BuscadorDePalabrasEnDiccionarios(new SuministradorEnMemoria(datos));
    }

    @Test
    @DisplayName("Contexto: un resultado encontrado | Acción: crearlo sin significados | Resultado esperado: no se puede")
    void unaPalabraEncontradaSinSignificadosNoExiste() {
        assertThrows(IllegalArgumentException.class, () -> new PalabraEncontrada("melón", "ES", List.of()));
    }

    @Test
    @DisplayName("Reglas de normalización: la ñ se queda, el resto de acentos no")
    void normalizacion() {
        assertEquals("membrillo", BuscadorDePalabrasEnDiccionarios.normalizarPalabra(" MEMBRILLO "));
        assertEquals("atun", BuscadorDePalabrasEnDiccionarios.normalizarPalabra("Atún"));
        assertEquals("pinguino", BuscadorDePalabrasEnDiccionarios.normalizarPalabra("pingüino"));
        assertEquals("año", BuscadorDePalabrasEnDiccionarios.normalizarPalabra("AÑO"));
        assertEquals("ES", BuscadorDePalabrasEnDiccionarios.normalizarIdioma(" es "));
    }

    @Test
    @DisplayName("Contexto: el diccionario falla al buscar | Acción: buscar | Resultado esperado: BuscadorDePalabrasException con la causa, no un 'no encontrada'")
    void unErrorDelDiccionarioEsUnaExcepcionDelServicio() {
        BuscadorDePalabras conFallo = new BuscadorDePalabrasEnDiccionarios(new SuministradorQueFalla());

        BuscadorDePalabrasException error = assertThrows(BuscadorDePalabrasException.class, () -> conFallo.buscarExacta("melón", "ES"));
        assertInstanceOf(IOException.class, error.getCause());
    }

    // Un idioma, ES, cuyo diccionario falla al buscar.
    @SuppressWarnings("removal")
    static class SuministradorQueFalla extends SuministradorEnMemoria {
        SuministradorQueFalla() {
            super(new DatosDePrueba().conDiccionario("ES"));
        }

        @Override public Optional<Diccionario> getDiccionarioBuena(String idioma) {
            return Optional.of(new Diccionario() {
                @Override public String getIdioma() { return "ES"; }
                @Override public boolean existe(String palabra) { return false; }
                @Override public Optional<List<Significado>> getSignificados(String palabra) { return Optional.empty(); }
                @Override public List<String> getPalabras() { return List.of(); }
                @Override public Optional<List<Significado>> buscarSignificados(String palabra) throws Exception {
                    throw new IOException("disco roto");
                }
            });
        }
    }
}
