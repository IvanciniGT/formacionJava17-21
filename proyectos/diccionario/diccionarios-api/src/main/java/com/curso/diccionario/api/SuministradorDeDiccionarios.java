package com.curso.diccionario.api;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;

public interface SuministradorDeDiccionarios {

    // Sin throws, un fallo (servidor caído, carpeta ilegible) solo se puede disfrazar de lista vacía,
    // y "no hay idiomas" no se distingue de "no he podido averiguarlo".
    // Java 9: @Deprecated(forRemoval = true): no solo "no lo uses", sino "se va a quitar".
    @Deprecated(since = "1.1.0", forRemoval = true)
    List<String> getIdiomas(); // Puede generar error

    // Método nuevo con default que delega en el viejo: las implementaciones existentes siguen
    // compilando sin tocarlas, y por eso esto es un minor (1.1.0), no un major.
    // Java 8: método default, para que el interfaz evolucione sin romper a quien ya lo implementa.
    default List<String> getIdiomasDisponibles() throws Exception {
        return getIdiomas();
    }

    // Existe aparte de getDiccionario porque una implementación puede saber qué idiomas
    // tiene sin llegar a cargar el diccionario.
    // Java 9: @Deprecated(forRemoval = true): no solo "no lo uses", sino "se va a quitar".
    @Deprecated(since = "1.1.0", forRemoval = true)
    boolean tienesDiccionarioDe(@NonNull String idioma); // Puede generar error

    // Java 8: método default, para que el interfaz evolucione sin romper a quien ya lo implementa.
    default boolean tienesDiccionarioDeIdioma(@NonNull String idioma) throws Exception{
        return tienesDiccionarioDe(idioma);
    } 

    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    @Deprecated(since = "1.1.0", forRemoval = true)
    Optional<Diccionario> getDiccionario(@NonNull String idioma); // Puede generar error

    default Optional<Diccionario> getDiccionarioBuena(@NonNull String idioma) throws Exception { // Puede generar error
        return getDiccionario(idioma);
    } // Java 1.8+
    // La gente no entiende para que existen los métodos default... 
    // Piensan que es para meter lógica compartida a todas las implementaciones.
    // Pero para meter logica compartida en implementaciones que existe en java desde la v1.1 se usaban clases abstractas.
    // Esto solo está con un objetivo: Facilitar la evolución de la API sin romper las implementaciones existentes.
    // Poder cambiar la api generando un minor, y no un major.


    // (Los resultados que pueden ser varias cosas distintas, y los sealed, están en el servicio:
    // ResultadoDeBusqueda, en diccionarios-service-api. Esto es un repositorio, de bajo nivel.)
}
