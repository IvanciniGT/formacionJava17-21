package com.curso.diccionario.service.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

// Qué es "parecida" es lógica de negocio: está en el servicio, no en el repositorio. El
// diccionario solo da la lista de palabras; ficheros y BBDD sugieren lo mismo porque el criterio
// está aquí, una sola vez.
//
// Interfaz y no clase: no se instancia nunca, y desde Java 9 una interfaz puede tener métodos
// privados estáticos para no exponer los detalles (antes había que hacer una clase final con
// constructor privado solo para agrupar funciones).
interface PalabrasSimilares {

    // Con 3 empiezan a salir sugerencias que no se parecen en nada: "pera" -> "mesa", "peso"...
    int DISTANCIA_MAXIMA = 2;
    int MAXIMO_DE_SUGERENCIAS = 10;

    static List<String> a(String objetivo, Collection<String> candidatas) {
        // Record local (Java 16): solo existe dentro de este método, que es el único que lo usa.
        record PalabraPuntuada(String palabra, int distancia) {}

        // Java 8: streams, el map-reduce de Java (stream, map, filter, collect...). En paralelo:
        // cada candidata se puntúa por su cuenta, así que se reparte entre los núcleos.
        return candidatas.parallelStream()
                // Filtro barato primero: si las longitudes se llevan más de DISTANCIA_MAXIMA,
                // la distancia también, y nos ahorramos calcularla para casi todo el diccionario.
                // Java 8: lambda, una función anónima escrita dentro de una expresión.
                .filter(candidata -> Math.abs(candidata.length() - objetivo.length()) <= DISTANCIA_MAXIMA)
                .filter(candidata -> !candidata.equals(objetivo))
                .map(candidata -> new PalabraPuntuada(candidata, distanciaDeLevenshtein(objetivo, candidata)))
                .filter(puntuada -> puntuada.distancia() <= DISTANCIA_MAXIMA)
                // Desempate alfabético: las candidatas suelen venir de un Map o de una consulta
                // sin orden, y sin esto dos llamadas iguales podrían sugerir en distinto orden.
                // Java 8: referencia a método (::), una lambda que solo llama a un método.
                .sorted(Comparator.comparingInt(PalabraPuntuada::distancia).thenComparing(PalabraPuntuada::palabra))
                .limit(MAXIMO_DE_SUGERENCIAS)
                .map(PalabraPuntuada::palabra)
                // Java 16: Stream.toList(), lista inmutable sin Collectors.toList().
                .toList();
    }

    // Cuántas letras hay que insertar, borrar o cambiar para pasar de una palabra a otra.
    // "melon" -> "melón" es 1 (cambiar la o); "pena" -> "perla" es 2 (cambiar la n, meter la l).
    // Java 9: método privado en una interfaz, para partir la lógica sin exponerla.
    private static int distanciaDeLevenshtein(String origen, String destino) {
        int[][] distancia = new int[origen.length() + 1][destino.length() + 1];
        for (int i = 0; i <= origen.length(); i++) {
            distancia[i][0] = i;
        }
        for (int j = 0; j <= destino.length(); j++) {
            distancia[0][j] = j;
        }
        for (int i = 1; i <= origen.length(); i++) {
            for (int j = 1; j <= destino.length(); j++) {
                int cambio = origen.charAt(i - 1) == destino.charAt(j - 1) ? 0 : 1;
                distancia[i][j] = Math.min(
                        Math.min(distancia[i - 1][j] + 1, distancia[i][j - 1] + 1),
                        distancia[i - 1][j - 1] + cambio);
            }
        }
        return distancia[origen.length()][destino.length()];
    }
}
