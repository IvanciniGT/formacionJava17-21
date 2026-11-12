package com.curso.diccionario.app.servidor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.Significado;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.api.contrato.DatosDePrueba;
import com.curso.diccionario.api.contrato.SignificadoDePrueba;

// Las pruebas de contrato piden un suministrador distinto en cada prueba, pero el servidor arranca
// una sola vez: el controlador recibe uno intercambiable, y cada prueba le cambia los datos.
@TestConfiguration
class ServidorConDatosDePrueba {

    @Bean
    @Primary
    SuministradorIntercambiable suministradorIntercambiable() {
        return new SuministradorIntercambiable();
    }

    @SuppressWarnings("removal")
    static class SuministradorIntercambiable implements SuministradorDeDiccionarios {

        // volatile: lo cambia el hilo de la prueba y lo leen los hilos del servidor.
        private volatile SuministradorDeDiccionarios delegado = new EnMemoria(new DatosDePrueba());

        void usar(DatosDePrueba datos) {
            delegado = new EnMemoria(datos);
        }

        @Override public List<String> getIdiomas() { return delegado.getIdiomas(); }
        @Override public boolean tienesDiccionarioDe(String idioma) { return delegado.tienesDiccionarioDe(idioma); }
        @Override public Optional<Diccionario> getDiccionario(String idioma) { return delegado.getDiccionario(idioma); }
    }

    // Lo más tonto que cumple el contrato (como la implementación de referencia del API, que no se reparte).
    // Java 16: record, clase inmutable: constructor, accesores, equals, hashCode y toString generados.
    @SuppressWarnings("removal")
    record EnMemoria(Map<String, Map<String, List<SignificadoDePrueba>>> diccionarios) implements SuministradorDeDiccionarios {

        EnMemoria(DatosDePrueba datos) {
            this(datos.getDiccionarios());
        }

        @Override public List<String> getIdiomas() { return List.copyOf(diccionarios.keySet()); }
        @Override public boolean tienesDiccionarioDe(String idioma) { return diccionarios.containsKey(idioma); }
        @Override public Optional<Diccionario> getDiccionario(String idioma) {
            return Optional.ofNullable(diccionarios.get(idioma)).map(palabras -> new Diccionario() {
                @Override public String getIdioma() { return idioma; }
                @Override public boolean existe(String palabra) { return palabras.containsKey(palabra); }
                @Override public Optional<List<Significado>> getSignificados(String palabra) {
                    return Optional.ofNullable(palabras.get(palabra)).map(significados -> significados.stream()
                            .<Significado>map(s -> new SignificadoEnMemoria(s.texto(), s.ejemplos())).toList());
                }
                @Override public List<String> getPalabras() { return List.copyOf(palabras.keySet()); }
            });
        }
    }

    record SignificadoEnMemoria(String getTexto, List<String> getEjemplos) implements Significado {}
}
