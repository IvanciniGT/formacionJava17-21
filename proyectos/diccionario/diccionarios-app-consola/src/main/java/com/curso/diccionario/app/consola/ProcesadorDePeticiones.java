package com.curso.diccionario.app.consola;

import java.util.List;
import java.util.Optional;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.service.api.BuscadorDePalabras;
import com.curso.diccionario.service.api.BuscadorDePalabrasException;
import com.curso.diccionario.service.api.IdiomaNoSoportado;
import com.curso.diccionario.service.api.PalabraEncontrada;
import com.curso.diccionario.service.api.PalabraNoEncontrada;
import com.curso.diccionario.ui.consola.api.InterfazDeUsuario;

// Lo que hace la app de consola: pedir los datos, preguntar al servicio y enseñar la respuesta.
// Ni cómo se habla con el usuario (eso es la InterfazDeUsuario) ni la lógica de negocio (eso es
// el BuscadorDePalabras): la consola solo EXPONE el servicio, igual que lo hará el controlador REST.
public class ProcesadorDePeticiones {

    // Códigos de salida del programa: un script que nos llame puede saber qué pasó sin leer la consola.
    public static final int TODO_BIEN = 0;
    public static final int ERROR_DE_USO = 1;
    public static final int ERROR_INTERNO = 2;

    private final InterfazDeUsuario interfaz;
    private final BuscadorDePalabras buscador;

    public ProcesadorDePeticiones(InterfazDeUsuario interfaz, BuscadorDePalabras buscador) {
        this.interfaz = interfaz;
        this.buscador = buscador;
    }

    public int procesarPeticion() {
        // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
        Optional<String> idioma = interfaz.obtenerIdiomaDelUsuario();
        Optional<String> palabra = interfaz.obtenerPalabraDelUsuario();
        if (idioma.isEmpty() || palabra.isEmpty()) {
            interfaz.mostrarErrorDeUsoDelPrograma();
            return ERROR_DE_USO;
        }
        try {
            buscar(palabra.get(), idioma.get());
            return TODO_BIEN;
        } catch (BuscadorDePalabrasException e) {
            interfaz.mostrarErrorGenerico(e);
            return ERROR_INTERNO;
        }
    }

    private void buscar(String palabra, String idioma) throws BuscadorDePalabrasException {
        // Java 21: switch con patrones sobre la interfaz sellada. Sin default a propósito: es
        // exhaustivo, así que si mañana el servicio añade otro resultado al permits, esto deja de
        // compilar y nos enteramos aquí, en vez de que el caso nuevo se cuele en silencio.
        // La flecha no es una lambda: es lo que se hace en cada caso.
        // Quien escribe en la consola espera que "Atun" encuentre "atún": de las dos búsquedas del
        // servicio, la que ignora mayúsculas y acentos. Se enseña la palabra del resultado, que es
        // como está escrita en el diccionario.
        switch (buscador.buscarIgnorandoMayusculasYAcentos(palabra, idioma)) {
            // Record pattern: desmonta el record en la propia rama.
            case PalabraEncontrada(String p, String i, List<Significado> significados) ->
                    interfaz.mostrarSignificadosDePalabra(i, p, significados);
            // Patrón de tipo y no record pattern: así siguió compilando cuando PalabraNoEncontrada
            // ganó sus sugerencias (servicio 1.1.0). Ahora solo hay que pasárselas a la interfaz.
            case PalabraNoEncontrada noEncontrada ->
                    interfaz.mostrarPalabraNoEncontrada(noEncontrada.idioma(), noEncontrada.palabra(), noEncontrada.sugerencias());
            case IdiomaNoSoportado noSoportado ->
                    interfaz.mostrarIdiomaNoEncontrado(noSoportado.idioma());
        }

        // Sin sealed ni switch con patrones había que hacerlo así (Java 16, instanceof con patrón).
        // Compila igual... y si falta un caso, también: ningún if avisa de que se ha quedado uno sin tratar.
        //
        // ResultadoDeBusqueda resultado = buscador.buscarIgnorandoMayusculasYAcentos(palabra, idioma);
        // if (resultado instanceof PalabraEncontrada encontrada) {
        //     interfaz.mostrarSignificadosDePalabra(encontrada.idioma(), encontrada.palabra(), encontrada.significados());
        // } else if (resultado instanceof PalabraNoEncontrada noEncontrada) {
        //     interfaz.mostrarPalabraNoEncontrada(noEncontrada.idioma(), noEncontrada.palabra(), noEncontrada.sugerencias());
        // } else if (resultado instanceof IdiomaNoSoportado noSoportado) {
        //     interfaz.mostrarIdiomaNoEncontrado(noSoportado.idioma());
        // }
    }

}
