// En java  1.8 aparece un nuevo paquete: java.util.function. Ese paquete define interfaces Funcionales
// Es decir Interfaces (Tipos de datos) que representan funciones.
// Cada una de estas interfaces ofrece UNA función que nos permite invocar a la función original que está siendo apuntada por la referencia.
//En cada interfa, el nombre de la función es distinto.
// - Function<T,R>   Representa una funación que acepta un argumento de tipo T y devuelve un resultado de tipo R
//          función interna: R apply(T t)
// - Consumer<T>     Representa una función que acepta un argumento de tipo T y no devuelve ningún resultado (void)
//          función interna: void accept(T t)
// - Supplier<R>     Representa una función que no acepta argumentos y devuelve un resultado de tipo R
//          función interna: R get()
// - Predicate<T>    Representa una función que acepta un argumento de tipo T y devuelve un valor booleano (true o false)
//          función interna: boolean test(T t)
// Dentro de este paqueta hay como 40 interfaces funcionales, variantes de estas: BiFunction<T,U,R>, BiConsumer<T,U>, BiPredicate<T,U>
import java.util.function.*;
import java.util.*;

public class ProgramacionFuncional {

    public static void main(String[] args) {
        System.out.println(generarSaludoFormal("Menchu"));
        System.out.println(generarSaludoInformal("Menchu"));
        imprimirSaludo(generarSaludoFormal("Menchu"));
        imprimirSaludo(generarSaludoInformal("Menchu"));

        Function<String,String> miVariable = ProgramacionFuncional::generarSaludoFormal; // Nuevo operador    :: Permite referenciar funciones.
        System.out.println(miVariable.apply("Federico")); // Aquí ejecutamos la función a la que nuestra variable apunta (referencia)

        imprimirSaludoGenerado(ProgramacionFuncional::generarSaludoFormal, "Felipe");
        imprimirSaludoGenerado(ProgramacionFuncional::generarSaludoInformal, "Felipe");
        // Imaginad que el código de generarSaludoInformal no lo quiero reusar.. solo lo quiero AQUI!
        // El tenerlo definido de forma TRADICIONAL.. por ahñi abajo.. en una fucnión, MEJORA LA LEGIBILIDAD?
        // Cuando usamos el paradigma funcional, a veces NO QUEREMOS reusar la función.. y el tenerlo definido de forma tradicional dificulta la legibilidad en ese contexto específico.
        // Es decir, en programación funcional, a veces queremos definir funciones por narices!
        // En estos casos,aparece en Java 1.8.. (y lo tienen casi tdos los lenguajes, un concepto nuevo: EXPRESION LAMBDA)
        // Una expresión es UN TROZO DE CODIGO QUE DEVUELVE UN VALOR.
        int numero = 5+5; // Esto es un statement (sentancia, Oración)
                     /// Esto es una expresión: 5+5 devuelve un valor que se asigna a la variable numero.
        // Una expresión lambda es un trozo de código que devuelve una referencia a una FUNCION definida dentro de la propia expresión.
        // Es una nueva sintaxis para definir FUNCIONES. FUNCIONES ANONIMAS!
        // Para ello, en Java 1.8 se añade un segundo operador: ->    - > ´a á  !=
        miVariable = (String nombre) -> {
            return "Qué pasa, " + nombre + "!";
        };
        // En java tiene gracia... hay muchas cosas que podemo obviar en esa sintaxis:
        miVariable = nombre -> { // En este caso, el String se infiere... de la variable
                                 // Tampoco pongo el tipo de dato devuelto. Se infiere también. De dónde? AQUI NO DE LA VARIABLE. Sino de el return.
            return "Qué pasa, " + nombre + "!";
        };
        miVariable = nombre -> "Qué pasa, " + nombre + "!"; // Cada vez que veaís una ->. digo: "Estoy DEFINIENDO UNA FUNCION que..."
        
        imprimirSaludoGenerado(nombre -> "Ey, " + nombre + "!", "Felipe"); // Esto si me ha mejorado la legibiliodad.
        // Ahora entiendo lo que sale por pantalla

        // Es duro al principio. El API entera de JAVA está migrando a programación funcional. Hay que meterse!

        // Ejemplos:

        // Pre java 1.5 cuando quería hacer un bucle:
        List<Integer> numeros = List.of(1,2,3,4,5,6) ; // Java 9 (incluido en java 11 como LTS) Métodos .of en todas las interfaces de colección.
                                                       // List.of, Map.of, Set.of son métodos estáticos que permiten crear colecciones inmutables de manera concisa.
        for (int i = 0; i < numeros.size(); i++) {
            System.out.println(numeros.get(i));
        }
        // Desde java 1.5 a java 1.,8: FOR - EACH (gracias a los iterables de java 1.5)
        for (Integer numero2 : numeros) {
            System.out.println(numero2);
        }
        // Desde java 1.8:
        numeros.forEach(System.out::println); // Uso de programación funcional con el método forEach de Iterable
        // De hecho esto es lo que se llama un bucle interno, y es más eficiente en muchos casos que un bucle externo tradicional.
        // ForEach tiene su lógica interna. Es una función que hace un for sobre cada elemento de la colección. Y llama en cada iteración a la función que le pasamos, con el elemento actual como argumento.

        List<Integer> numeros2 = List.of(1,2,3,4,5,6,7,8,9);
        System.out.println("Números multiplicados por 3 y filtrados por pares:");
        numeros2.stream()
                .map(           n -> n * 3              ) // Multiplica cada número por 3
                .filter(        n -> n % 2 == 0         ) // Mantén solo los números pares
                .forEach(       System.out::println     ); // Imprime los números pares

    }



    public static void imprimirSaludo(String saludo) {
        System.out.println(saludo);
    }

    public static void imprimirSaludoGenerado(Function<String,String> funcionGeneradoraDeSaludos, String nombre) {
        String saludo = funcionGeneradoraDeSaludos.apply(nombre);
        System.out.println(saludo);
    }



    public static String generarSaludoFormal(String nombre) {
        return "Hola, " + nombre + ". Es un placer conocerte.";
    }

    public static String generarSaludoInformal(String nombre) {
        return "Qué pasa, " + nombre + "!";
    }


}
