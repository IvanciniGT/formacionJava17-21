package com.curso.diccionario.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.service.api.BuscadorDePalabras;
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
}
