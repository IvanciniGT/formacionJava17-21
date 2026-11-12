package com.curso.diccionario.api;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;

public interface Diccionario {

    String getIdioma();

    // 1.1.0: los métodos de la 1.0.0 no tienen por dónde avisar de un error (un fichero que no se
    // puede leer, una BBDD caída...), y un fallo acaba pareciendo "no existe". Cada uno tiene ahora
    // un hermano que sí lanza, con un default que delega en el viejo: las implementaciones que ya
    // existen siguen compilando sin tocarlas, y por eso esto es un minor y no un major.

    // Java 9: @Deprecated(forRemoval = true): no solo "no lo uses", sino "se va a quitar".
    @Deprecated(since = "1.1.0", forRemoval = true)
    boolean existe(@NonNull String palabra);

    // Java 8: método default, para que el interfaz evolucione sin romper a quien ya lo implementa.
    default boolean existeLaPalabra(@NonNull String palabra) throws Exception {
        return existe(palabra);
    }

    // Vacío si la palabra no existe; si existe, al menos un significado.
    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    @Deprecated(since = "1.1.0", forRemoval = true)
    Optional<List<Significado>> getSignificados(@NonNull String palabra);

    default Optional<List<Significado>> buscarSignificados(@NonNull String palabra) throws Exception {
        return getSignificados(palabra);
    }

    // Todas las palabras del diccionario, tal cual están escritas (con sus acentos).
    // Un repositorio de bajo nivel: es casi un SELECT palabra FROM palabras WHERE idioma = ?.
    // Lo que se haga con ellas (buscar sin acentos, sugerir parecidas) es lógica de negocio:
    // eso es cosa del servicio, no de aquí.
    @Deprecated(since = "1.1.0", forRemoval = true)
    List<String> getPalabras();

    default List<String> listarPalabras() throws Exception {
        return getPalabras();
    }

}
