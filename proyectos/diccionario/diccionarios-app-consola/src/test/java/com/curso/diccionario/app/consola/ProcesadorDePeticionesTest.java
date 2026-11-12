package com.curso.diccionario.app.consola;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.api.BuscadorDePalabrasException;
import com.curso.diccionario.service.api.IdiomaNoSoportado;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.api.PalabraNoEncontrada;
import com.curso.diccionario.service.api.ResultadoDeBusqueda;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// La razón de separar la interfaz de usuario y el servicio: la lógica de la consola se prueba sin
// capturar la consola y sin diccionarios, mirando qué se le pidió mostrar a una interfaz falsa.
class ProcesadorDePeticionesTest {

    // Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
    record SignificadoDePrueba(String getTexto, List<String> getEjemplos) implements Significado {}

    static final List<Significado> SIGNIFICADOS_DE_MELON = List.of(new SignificadoDePrueba("Fruto.", List.of()));

    // Apunta cada cosa que se le pide mostrar, como texto fácil de comparar.
    static class InterfazQueApunta implements InterfazDeUsuario {
        final List<String> mostrado = new ArrayList<>();
        final Optional<String> idioma;
        final Optional<String> palabra;

        InterfazQueApunta(String idioma, String palabra) {
            this.idioma = Optional.ofNullable(idioma);
            this.palabra = Optional.ofNullable(palabra);
        }

        @Override public Optional<String> obtenerIdiomaDelUsuario() { return idioma; }
        @Override public Optional<String> obtenerPalabraDelUsuario() { return palabra; }
        @Override public void mostrarSignificadosDePalabra(String i, String p, List<Significado> s) { mostrado.add("significados " + i + " " + p + " " + s.size()); }
        @Override public void mostrarPalabraNoEncontrada(String i, String p) { mostrado.add("palabra no encontrada " + i + " " + p); }
        @Override public void mostrarPalabraNoEncontrada(String i, String p, List<String> sugerencias) {
            mostrado.add("palabra no encontrada " + i + " " + p + " sugerencias " + sugerencias);
        }
        @Override public void mostrarIdiomaNoEncontrado(String i) { mostrado.add("idioma no encontrado " + i); }
        @Override public void mostrarErrorDeUsoDelPrograma() { mostrado.add("error de uso"); }
        @Override public void mostrarErrorGenerico(Exception e) { mostrado.add("error " + e.getMessage()); }
    }

    // Un servicio que responde siempre lo mismo, o falla si se le da una excepción.
    static class BuscadorFijo implements BuscadorDePalabras {
        final ResultadoDeBusqueda resultado;
        final BuscadorDePalabrasException error;

        BuscadorFijo(ResultadoDeBusqueda resultado) { this.resultado = resultado; this.error = null; }
        BuscadorFijo(BuscadorDePalabrasException error) { this.resultado = null; this.error = error; }

        @Override public List<String> idiomasDisponibles() { return List.of("ES"); }
        @Override public boolean idiomaSoportado(String idioma) { return true; }
        @Override public ResultadoDeBusqueda buscarExacta(String palabra, String idioma) throws BuscadorDePalabrasException {
            return buscarIgnorandoMayusculasYAcentos(palabra, idioma);
        }
        @Override public ResultadoDeBusqueda buscarIgnorandoMayusculasYAcentos(String palabra, String idioma) throws BuscadorDePalabrasException {
            if (error != null) {
                throw error;
            }
            return resultado;
        }
    }

    private static int procesar(InterfazQueApunta interfaz, BuscadorFijo buscador) {
        return new ProcesadorDePeticiones(interfaz, buscador).procesarPeticion();
    }

    @Test
    @DisplayName("Contexto: el servicio encuentra melón | Acción: procesar | Resultado esperado: se muestran sus significados y sale con 0")
    void unaPalabraQueExisteMuestraSusSignificados() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", "melón");

        int salida = procesar(interfaz, new BuscadorFijo(new PalabraEncontrada("melón", "ES", SIGNIFICADOS_DE_MELON)));

        assertEquals(List.of("significados ES melón 1"), interfaz.mostrado);
        assertEquals(ProcesadorDePeticiones.TODO_BIEN, salida);
    }

    @Test
    @DisplayName("Contexto: el servicio no encuentra patata | Acción: procesar | Resultado esperado: palabra no encontrada y sale con 0")
    void unaPalabraQueNoExiste() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", "patata");

        int salida = procesar(interfaz, new BuscadorFijo(new PalabraNoEncontrada("patata", "ES", List.of())));

        assertEquals(List.of("palabra no encontrada ES patata sugerencias []"), interfaz.mostrado);
        assertEquals(ProcesadorDePeticiones.TODO_BIEN, salida);
    }

    @Test
    @DisplayName("Contexto: el servicio no soporta XX | Acción: procesar | Resultado esperado: idioma no encontrado")
    void unIdiomaSinDiccionario() {
        InterfazQueApunta interfaz = new InterfazQueApunta("XX", "gato");

        procesar(interfaz, new BuscadorFijo(new IdiomaNoSoportado("gato", "XX")));

        assertEquals(List.of("idioma no encontrado XX"), interfaz.mostrado);
    }

    @Test
    @DisplayName("Contexto: el usuario escribe 'es MELON' y el servicio encuentra melón | Acción: procesar | Resultado esperado: se muestra la palabra del diccionario")
    void seMuestraLoQueBuscoElServicio() {
        InterfazQueApunta interfaz = new InterfazQueApunta("es", "MELON");

        procesar(interfaz, new BuscadorFijo(new PalabraEncontrada("melón", "ES", SIGNIFICADOS_DE_MELON)));

        assertEquals(List.of("significados ES melón 1"), interfaz.mostrado);
    }

    @Test
    @DisplayName("Contexto: el usuario no da la palabra | Acción: procesar | Resultado esperado: error de uso, sale con 1 y no se busca nada")
    void sinPalabraEsUnErrorDeUso() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", null);

        int salida = procesar(interfaz, new BuscadorFijo(new PalabraNoEncontrada("x", "ES", List.of())));

        assertEquals(List.of("error de uso"), interfaz.mostrado);
        assertEquals(ProcesadorDePeticiones.ERROR_DE_USO, salida);
    }

    @Test
    @DisplayName("Contexto: el servicio falla | Acción: procesar | Resultado esperado: error genérico con el motivo y sale con 2")
    void unErrorDelServicioSeMuestraComoError() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", "melón");

        int salida = procesar(interfaz, new BuscadorFijo(new BuscadorDePalabrasException("disco roto", new IOException("sector ilegible"))));

        assertEquals(List.of("error disco roto"), interfaz.mostrado);
        assertEquals(ProcesadorDePeticiones.ERROR_INTERNO, salida);
    }

    @Test
    @DisplayName("Contexto: melon no existe y el servicio sugiere melón | Acción: procesar | Resultado esperado: palabra no encontrada con su sugerencia")
    void unaPalabraQueNoExisteLlevaSusSugerencias() {
        InterfazQueApunta interfaz = new InterfazQueApunta("ES", "melon");

        procesar(interfaz, new BuscadorFijo(new PalabraNoEncontrada("melon", "ES", List.of("melón"))));

        assertEquals(List.of("palabra no encontrada ES melon sugerencias [melón]"), interfaz.mostrado);
    }
}
