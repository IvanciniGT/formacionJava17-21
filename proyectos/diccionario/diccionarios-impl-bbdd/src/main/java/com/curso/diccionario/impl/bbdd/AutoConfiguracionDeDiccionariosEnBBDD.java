package com.curso.diccionario.impl.bbdd;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.impl.bbdd.modelo.RepositorioDeDiccionarios;
import com.curso.diccionario.impl.bbdd.modelo.RepositorioDePalabras;

// Lo que hace que baste con añadir el jar: Spring Boot la encuentra en
// META-INF/spring/...AutoConfiguration.imports y monta todo sin que la aplicación lo nombre.
//
// @AutoConfigurationPackage: añade este paquete a los que Spring Boot escanea buscando entidades y
// repositorios. Sin él solo miraría el paquete de la aplicación, y este jar está en otro.
// Antes que JPA: los paquetes tienen que estar apuntados cuando JPA los busque.
@AutoConfiguration(beforeName = {
        "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
        "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration" })
@AutoConfigurationPackage
public class AutoConfiguracionDeDiccionariosEnBBDD {

    @Bean
    AlmacenDeDiccionarios almacenDeDiccionarios(RepositorioDeDiccionarios diccionarios, RepositorioDePalabras palabras) {
        return new AlmacenDeDiccionarios(diccionarios, palabras);
    }

    // @Primary: si en el mismo ensamblado hay otro repositorio (el de ficheros, que el cargador
    // necesita para leer los ficheros), el que usa el servicio es este.
    @Bean
    @Primary
    SuministradorDeDiccionarios suministradorDeDiccionariosEnBBDD(AlmacenDeDiccionarios almacen) {
        return new SuministradorDeDiccionariosEnBBDD(almacen);
    }
}
