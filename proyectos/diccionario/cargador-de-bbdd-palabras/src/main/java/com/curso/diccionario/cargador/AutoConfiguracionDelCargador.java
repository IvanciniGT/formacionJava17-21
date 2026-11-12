package com.curso.diccionario.cargador;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.support.TransactionTemplate;

import com.curso.diccionario.cargador.modelo.RepositorioDeHuellas;
import com.curso.diccionario.impl.bbdd.AlmacenDeDiccionarios;

// Como el repositorio en BBDD: basta con añadir el jar. Spring Boot la encuentra en
// META-INF/spring/...AutoConfiguration.imports, apunta este paquete para las entidades (la tabla de
// huellas) y arranca el cargador al levantar la aplicación (es un ApplicationRunner).
@AutoConfiguration(beforeName = {
        "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
        "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration" })
@AutoConfigurationPackage
public class AutoConfiguracionDelCargador {

    // ConditionalOnMissingBean: si la aplicación (o una prueba) trae sus propios ficheros, se usan los suyos.
    @Bean
    @ConditionalOnMissingBean
    FuenteDeDiccionarios fuenteDeDiccionarios() {
        return new FuenteDeDiccionariosEnClasspath();
    }

    @Bean
    CargadorDeDiccionarios cargadorDeDiccionarios(FuenteDeDiccionarios fuente, AlmacenDeDiccionarios almacen,
            RepositorioDeHuellas huellas, TransactionTemplate transacciones) {
        return new CargadorDeDiccionarios(fuente, almacen, huellas, transacciones);
    }
}
