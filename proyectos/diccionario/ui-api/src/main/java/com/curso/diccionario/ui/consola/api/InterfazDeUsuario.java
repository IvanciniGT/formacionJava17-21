package com.curso.diccionario.ui.consola.api;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Significado;

// Todo lo que la aplicación le dice o le pide al usuario. La lógica (qué decir y cuándo) no está
// aquí: está en quien la usa. Así se puede cambiar cómo se habla con el usuario (argumentos,
// teclado, otro idioma) sin tocar la lógica, y probar la lógica sin capturar la consola.
public interface InterfazDeUsuario {

    // Vacío si el usuario no lo ha dado: decidir qué hacer entonces es lógica, no interfaz.
    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    Optional<String> obtenerIdiomaDelUsuario();

    Optional<String> obtenerPalabraDelUsuario();

    void mostrarSignificadosDePalabra(String idioma, String palabra, List<Significado> significados);

    void mostrarPalabraNoEncontrada(String idioma, String palabra);

    // 1.1.0: con las palabras parecidas que se le pueden sugerir al usuario (puede venir vacía).
    // default que delega en el de antes: una interfaz de usuario que no sepa enseñar sugerencias
    // sigue compilando y funcionando, solo que no las enseña. Minor, no major.
    // Java 8: método default, para que el interfaz evolucione sin romper a quien ya lo implementa.
    default void mostrarPalabraNoEncontrada(String idioma, String palabra, List<String> sugerencias) {
        mostrarPalabraNoEncontrada(idioma, palabra);
    }

    void mostrarIdiomaNoEncontrado(String idioma);

    void mostrarErrorDeUsoDelPrograma();

    void mostrarErrorGenerico(Exception error);
}
