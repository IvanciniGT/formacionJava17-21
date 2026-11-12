package com.curso.diccionario.app.servidor;

import java.util.ServiceLoader;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.controlador.rest.ControladorRestDiccionarios;
import com.curso.diccionario.controlador.rest.GestorDeErroresRest;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.impl.BuscadorDePalabrasEnDiccionarios;

// Las FACTORÍAS de la app de consola, convertidas en Spring en BEANS. Aquí no se hace el new del
// controlador: lo hace Spring (inversión de control), y para hacerlo necesita un BuscadorDePalabras.
// Estos métodos @Bean le dicen de dónde sale cada pieza, y Spring se las inyecta a quien las pida
// en su constructor (inyección de dependencias).
@Configuration
// El controlador vive en otro paquete y el escaneo de @SpringBootApplication no llega:
// se importa explícitamente, así se ve qué piezas monta la aplicación.
@Import({ ControladorRestDiccionarios.class, GestorDeErroresRest.class })
public class ConfiguracionDeDiccionarios {

    // El repositorio: el que lleve el ensamblado. Igual que la factoría de la consola, no nombra
    // ninguna implementación: se lo pide al ServiceLoader (fuera del module path, el que se anuncie
    // en META-INF/services). Si el ensamblado no lleva ninguno así, este bean no se crea, y el
    // repositorio tendrá que venir de otro sitio (de la autoconfiguración de Spring de su jar).
    @Bean
    @ConditionalOnResource(resources = "classpath:META-INF/services/com.curso.diccionario.api.SuministradorDeDiccionarios")
    SuministradorDeDiccionarios suministradorDeDiccionarios() {
        return ServiceLoader.load(SuministradorDeDiccionarios.class)
                .findFirst()
                // Java 8: lambda, una función anónima escrita dentro de una expresión.
                .orElseThrow(() -> new IllegalStateException("No hay ningún SuministradorDeDiccionarios en el classpath"));
    }

    // El servicio de búsqueda, el mismo que usa la app de consola en local, sobre ese repositorio.
    // El controlador solo lo expone por HTTP.
    @Bean
    BuscadorDePalabras buscadorDePalabras(SuministradorDeDiccionarios suministrador) {
        return new BuscadorDePalabrasEnDiccionarios(suministrador);
    }
}
