
# Java LTS (Long Term Support)

1.0
1.1
1.2
...
1.8 <--- LTS
---
9
10
11 <--- LTS
...
17 <--- LTS
...
21 <--- LTS
...
27

Los lenguajes de programación no evolucionan por evolucionar.
Aparecen nuevos problemas, nuevas formas de trabajo, nuevas arquitecturas.
Java evoluciona para adaptarse a estos cambios, ofreciendo nuevas características y mejoras en cada versión.
Los cambios que se producen en el lenguaje son reflejo de las necesidades y tendencias del desarrollo de software en cada momento.

Hoy en día:
- Trabajamos con arquitecturas de componentes desacoplados.
- Trabajamos con metodologías ágiles.
- Trabajamos con pipelines CI/CD.
- Respetamos ciertos principios de diseño y buenas prácticas en el desarrollo de software.
- ...

Muchas de las cosas que hablaremos NO PODREIS USARLAS EN VUESTROS PROYECTOS ACTUALES.

Para montar un monolito, usando metodologías tradicionales y sin pipelines CI/CD, con Java 1.8 estoy perfecto!

---

# Java 1.8

Fué junto con Java 9 la versión de Java con más cambios (o al menos con cambios de mayor calado).
Aquí hablamos más de funcionalidades básicas del lenguaje.

Lo más importante con diferencia es el soporte para programación funcional.

No estamos hablando de programación con funciones puras al estilo de lenguajes funcionales como Haskell.
Estamos hablando de poder tratar a las funciones como ciudadanos de primera clase.

En desarrollo de software hablamos de paradigmas de programación. Básicamente un paradigma es un nombre hortera que los desarrolladores se inventan para nombrar a las distintas formas en las que usamos un lenguaje de programación. Pero es algo que tenemos en los lenguajes humanos.

> Menchu, pon una silla debajo de la ventana!                                           IMPERATIVO
> Menchu, debajo de la ventana tiene que haber una silla. Es tu responsabilidad.        DECLARATIVO


Estamos muy acostumbrados a ciertos paradigmas:
- Imperativo:                       le decimos al ordenador qué tiene que hacer paso a paso. A veces queremos romper la secuencialidad, 
                                    y nos salen las típicas excepciones de control de flujo: condicionales, bucles, control de excepciones, etc.

    JAVA soporta el paradigma imperativo a través de instrucciones secuenciales, condicionales, bucles y control de excepciones.

- Procedural:                       La habilidad de agrupar secuencias de instrucciones bajo un nombre. 
                                    Y poder invocarlas posteriormente a través de ese nombre. Función, Procedimiento, Método, Subrutina.
                                    Qué aporta el crear funciones?
                                    - Mejorar la legibilidad del código -> Mejorar la mantenibilidad
                                    - Poder reusar el código

    JAVA soporta el paradigma procedural a través de métodos y funciones.

- Orientado a Objetos:              Todo lenguaje maneja datos. Y esos datos son de diferentes tipos.
                                    Todo lenguaje vienen con un catálogo de tipos de datos. Hay lenguajes que me permiten definir mis propios tipos de datos, con sus propiedades y sus comportamientos.

                                                            Se caracteríza por                     Admite unos comportamientos
                                        String (Textos)     Una secuencia de caracteres             length(), charAt(), substring() aMayúsculas()
                                        List (Listas)       Una colección ordenada de elementos     add(), get(), remove(), size()
                                        Fecha               Dia / Mes / Año                         caesEnJueves() caesEnBisisesto()

                                    Ahora.. quizás yo quiero otros tipos de datos, con sus propiedades y comportamientos. Eso es lo que nos permite la programación orientada a objetos: definir nuestras propias clases y objetos.

                                        Usuario
                                            - Propiedades: nombre, email, fechaNacimiento
                                            - Comportamientos: saludar(), esMayorDeEdad()

                                        RepositorioDeUsuarios
                                            - Propiedades: listaDeUsuarios
                                            - Comportamientos: agregarUsuario(), eliminarUsuario(), buscarUsuario()

                                        ServicioDeUsuarios
                                            - Propiedades: repositorioDeUsuarios
                                            - Comportamientos: registrarUsuario(), eliminarUsuario(), buscarUsuario()

Funcional:                          Cuando en lenguaje me permite que una variable punte a una función para posteriormente ejecutar la función 
                                    desde la variable, decimos que el lenguaje soporta el paradigma funcional.


En paralelo con esto de la progframación funcional, en Java 1.8 ocurren más cosas: 
- Se incluye una imp`lementación del modelo de programación MAP-REDUCE

## Map-Reduce

Es un modelo de programaicón para colecciones de datos.
Cuado quiero hacer operaciones sobre colecciones de datos, MAP REDUCE es una forma de escribir esos algoritmos que es mucho más eficiente en su ejecución que la programación IMPERATIVA tradicional. Esto lo crea GOOGLE! Y de hecho es la base del mundo BIGDATA.
En todo lenguaje de programación, se incluye de una u otra forma implementaciones del modelo de programación MAP-REDUCE.
En cSharp se puede implementar el modelo MAP-REDUCE utilizando LINQ (Language Integrated Query), que permite realizar operaciones de transformación y agregación sobre colecciones de datos de manera funcional. Por ejemplo, se pueden usar los métodos Select (MAP) y Aggregate (REDUCE) para procesar listas de manera eficiente.

En java la implementación se hace mediante la clase `java.util.stream.Stream`, que permite realizar operaciones de transformación y agregación sobre colecciones de datos de manera funcional. Por ejemplo, se pueden usar los métodos `map` (MAP) y `reduce` (REDUCE) para procesar listas de manera eficiente.

Un Stream (java 1.8) es como una lista de elementos... también trabaja con genéricos:
Stream<T> // T es el tipo de los elementos del Stream. Por ejemplo, Stream<Integer> para un Stream de enteros.

Lo que pasa es que en las lista, las operaciones que tengo son del tipo:
- Añade uno
- Inserta uno entre estos 2
- Borra uno
- Dame el tercero
- Cuántos hay

En los Stream hay operaciones map/reduce:
- Transforma de una todos los elementos de la colección (MAP)
- Mantén solo los elementos que cumplen una condicón (FILTER)
- Ordena todos los elemento en base a un criterio (SORT)
- Combina todos los elementos en un solo valor (REDUCE)
Dependiendo de la implementación, tenemos hasta más de 50 operaciones MapReduce. Aquí no las voy a nombrar.

Cualquier Collection se puede transformar a un Stream, mediante la función `stream()`. Por ejemplo:

```java
List<Integer> lista = List.of(1, 2, 3, 4, 5);
Stream<Integer> stream = lista.stream();
```

Cualquier Stream lo puedo convertir en una colección tradicional... a través del método .collect(). Por ejemplo:

```java
List<Integer> lista2 = stream.collect(Collectors.toList());
Set<Integer> conjunto = stream.collect(Collectors.toSet());
```

En versiones de Java posteriores, se incluyen algunos métodos de utilidad para trabajar con Streams, como `toList()`, `toSet()`, `toMap()`, entre otros. Por ejemplo:

```java
List<Integer> lista3 = stream.toList();
```

## Optional, métodos default en interfaces y cumplimiento de Liskov!

Este conjunto de cambios comienzan en Java 1.8... pero se prolongan en versiones posteriores. 
Algunas de estas cosas que os voy a comentar, existían en .net csharp desde v1 y v2... Otras no existen en .net ni a día de hoy.

Liskov. Habéis oído hablar de los principios SOLID de desarrollo de software?
Es una colección de 5 principios que SI LOS RESPECTO, me garantizan que mi código será más fácil de mantener en el futuro.
Y esto es clave. Y no lo tenemos muchas veces en cuenta... o al menos tanto como deberíamos.

Que un programa funcone: SE DA POR DESCONTADO. No hay nada especial en ello.
Cuando empezamos a programar, NOS CENTRAMOS en conseguir un código que funcione.
PERO REALMENTE LO IMPORTANTE NO ES ESO (eso se da por descontado). Lo importante es que el código sea mantenible, flexible y fácil de entender a largo plazo. Esto es la diferencia entre un desarrollador junior y un desarrollador senior.

Que un COCHE ANDE se da por descontado... No es nada especial.

SOLID es un acrónimo:
- S: Single Responsibility Principle (Principio de Responsabilidad Única)
- O: Open/Closed Principle (Principio de Abierto/Cerrado)
- L: (Barbara) Liskov Substitution Principle (Principio de Sustitución de Liskov)
- I: Interface Segregation Principle (Principio de Segregación de Interfaces)
- D: Dependency Inversion Principle (Principio de Inversión de Dependencias)

El principio de sustitución de Liskov establece que los objetos de una clase derivada deben poder sustituir a los objetos de la clase base sin alterar el correcto funcionamiento del programa. En otras palabras, si una clase B hereda de una clase A, deberíamos poder usar objetos de B en lugar de objetos de A sin que el comportamiento esperado del programa se vea afectado.

En Cristiano, debería poder reemplazar componentes de mi sistema por otros equivalentes (que cumplan con la misma interfaz o contrato) sin que el comportamiento del sistema se vea afectado.
No es un patrón! No me dice COMO ESCRIBIR EL código, solo me dice que tendría que tener en la cabeza cuando creo código para facilitar su mantenimiento y evolución.

```java
/* Interface Diccionario para sacar las definiciones de palabras que existan en un determinado idioma 
*  Potenciales implementaciones?
*  - DiccionarioConLasPalabrasGuardadasEnFicherosDeTexto
*  - DiccionarioConLasPalabrasGuardadasEnBaseDeDatos
*/

public interface Diccionario {

    String getIdioma();

    boolean existePalabra(String palabra); // Devuelve true si la palabra existe en el diccionario, false en caso contrario.

    List<String> getDefiniciones(String palabra) throws NoSuchWordException ; // ESTA FUNCION ES UNA CAGADA MONUMENTAL!

}
```

Qué problemas tiene esa función (especialmente en JAVA.... en scharp hasta colaría)?
El primer problema es que NADIE de los que estáis aquí sabe comunicarse con esa función. Es imposible. El contrato no lo cierra.
Qué le tengo que pasar a la función según su definición? Un String representado la palabra.
Qué me devuelve la función?                              Una Lista de String que representa las definiciones de la palabra pasada como argumento.
                                                         ESTO ES MENTIRA!
- Qué me devuelve si le paso la palabra "archilococo" al diccionario de Español? Es una palabra NO EXISTE!
  Qué devuelve? NPI... qué opciones habría?
  - lanzar una Exception        No es ambigua... pero ESTO USANDO UNA EXCEPTION (Que es muy cara computacionalmente) para lógica de contro, y eso es una mala práctica
    - Lanzar una excepcion lo primero implica hacer un volcado de la pila (stack trace) de métodos. EIN? PAra decir que no existe una palabra?
    - Además, las excepciones son para casos NO CONTROLABLES **A PRIORI**.
    - No puedo saber si una BBDD está operativa o no hasta que INTENTE (TRY) conectarme. Por eso LO INTENTO (TRY) y me puedo llevar SORPRESITA (Exception)
    - No puedo saber si un fichero existe o si tengo permisos de lectura sobre él hasta que lo intento abrir. Por eso LO INTENTO (TRY) y me puedo llevar SORPRESITA (Exception)
    - Coño... es fácil saber a PRIORI si una palabra existe o no en el diccionario, no hace falta lanzar una excepción para eso.
  - lanzar una RuntimeException \
  - Una lista de Strings vacía   \
  - Un null                      / Es ambiguo... la firma del método (signature) no deja clara cuál es la intención en caso de que la palabra no exista.


El contrato no cierra el comportamiento de la función. Por ende tampoco queda cerrado lo que deben implementar las distintas implementaciones de la interfaz.
Por ejemplo:
- DiccionarioConLasPalabrasGuardadasEnFicherosDeTexto, podría devolver una lista vacía si no encuentra la palabra
- DiccionarioConLasPalabrasGuardadasEnBaseDeDatos, podría devolver un null

Alguién CONSUMIRA Diccionario (Lo usará para buscar palabras)

```java
Diccionario miDiccionario = ???; // Tengo un diccionario
List<String> definiciones = miDiccionario.getDefiniciones("archilococo");
if(definiciones.isEmpty()) {
    System.out.println("No se encontraron definiciones para la palabra 'archilococo'");
}
```
Este código funcionaría con la implementación DiccionarioConLasPalabrasGuardadasEnFicherosDeTexto, porque esta implementación devuelve una lista vacía si no encuentra la palabra.
En cambio si cambiase el diccionario a DiccionarioConLasPalabrasGuardadasEnBaseDeDatos, este código fallaría! No se comportaría igual... no me diría que no se encontraron definiciones, porque en lugar de devolver una lista vacía, devolvería un null.

El problema grande es que JAVA pre 1.8 no me ofrecía NINGUNA ALTERNATIVA para resolver este problema.
Al crear un código debía de:
- Mirar la implementación concreta de Diccionario para saber cómo manejar los casos en los que la palabra no existe. (caso que lo tenga disponible)
- O mirar su documentación para ver cómo se comporta en esos casos. (caso que la hayan creado)

En serio? Llevamos 60-7'-80 años creando software.. y tengo que mirar una documentaciuón o un código para saber cómo llamar a una triste función?
VAYA MIERDA DE LENGUAJE JAVA! De hecho JAVA tiene una cantidad enorme DE CAGADAS en su gramática.
Que poco a poco, algunas se han limpiando... No todas...
Pero está bien.. por eso van saliendo versiones.

Decisión en JAVA 1.8:
- DESDE JAVA 1.8 SE CONSIDERA UNA MUY MALA PRACTICA QUE UNA FUNCION DEVUELVA null. Por convenio!
- Para evitar el uso de nulls secrea una clase nueva (que en csharp existe de toda la vida - Nullable<T>) llamada Optional<T>, que puede contener un valor o estar vacía.
- NOTA: En  csharp además, tienen una sintaxis muy códoda: T? para Nullable<T>

```java
public interface Diccionario {

    String getIdioma();

    boolean existePalabra(String palabra); // Devuelve true si la palabra existe en el diccionario, false en caso contrario.

    Optional<List<String>> getDefiniciones(String palabra);
    //Te devolveré una Lista de textos o NO!
}
```

Un Optional es como una CAJA!
La CAJA me lla devuelven siempre... pero puede tener algo dentro o no! YA NO HAY AMBIGUEDAD

Cuando me den lacaja, miro si tiene algo (.isPresent()) o si está vacía (.isEmpty()).
Caso que tenga algo, puedo sacarlo (.get()).. Si intento sacarlo y no tiene nada dentro, me dará una exception: NoSuchElementException.

Incluso, puedo intentar sacarlo, dando un valor por defecto en caso de que esté vacío:

```java
List<String> definiciones = miDiccionario.getDefiniciones("archilococo").orElse(Collections.emptyList());
```

Incluso hay funciones aplicando programación funcional para establecer el valor por defecto:

```java
List<String> definiciones = miDiccionario.getDefiniciones("archilococo").orElseGet(() -> Collections.emptyList());
```

Hay mucha gente que NO ENTIENDE el uso de Optional... y dicen que es para evitar NullPointerExceptions. Mentira!
No me quita el chequeo.. solo que ahora si no hago el chequeo no tengo un NullPointerException tengo un NoSuchElementException.

El Optional SOLO VALE PARA QUITAR AMBIGUEDAD... CERRAR EL CONTRATO!

YA está cerrado el contrato?

```java
public interface Diccionario {

    String getIdioma();

    boolean existePalabra(String palabra); // Devuelve true si la palabra existe en el diccionario, false en caso contrario.

    Optional<List<String>> getDefiniciones(String palabra);
    //Te devolveré una Lista de textos o NO!
}
```

Aún NO!
Por desgracia en JAVA aún... 27 versiones después NO HAN RESUELTO LA OTRA PARTE! En .net lleva resuelta mucho tiempo.
- Qué pasa si a la función le paso un null como palabra? Ne devuelve una Optional vacío? Lanza una NullPointerException? No está definido en el contrato!

El contrado debe ser claro en lo que tengo que pasar, y lo que va a devolver.. En el comportamiento esperado en todos los escenarios posibles.
En JAVA, para estas cosas tiramos de ANOTACIONES y de librerías externas.
Por ejemplo, podemos usar `@NonNull` o `@Nullable` para indicar si un parámetro puede ser null o no, y así cerrar el contrato de manera más explícita.
@NonNull -> Lombok y otras librerías externas proporcionan esta anotación para indicar que un parámetro no puede ser null.
@Nullable -> JSpecification y otras librerías externas proporcionan esta anotación para indicar que un parámetro puede ser null.

Las anotaciones de java (en .net las llamamos atributos) ofrecen lenguaje DECLARATIVO!

```java
public interface Diccionario {

    String getIdioma();

    boolean existePalabra(@NonNull String palabra); // Devuelve true si la palabra existe en el diccionario, false en caso contrario.

    Optional<List<String>> getDefiniciones(@NonNull String palabra);
    //Te devolveré una Lista de textos o NO!
}
```

Esto es cerrar el contrato!

El caso de los argumentos sigue si solución en el lenguaje... en la industria se la hemos dado ya la solución (vía anotaciones como @NonNull y @Nullable ofrecidas por librerías externas)... y esto nos va a pasar bastante en el curso... Hay muchas cosas que se ha introducido en JAVA, que la industria ya había resuelto. Han llegado tarde y mal!

## Setters y Getters de JAVA

Que siguen sin solución en JAVA.
Son una cagada enorme del lenguaje. Son una carencia de JAVA.

```java
public class Persona {
    public String nombre;
    public int edad;
}

Persona persona = new Persona();
persona.nombre = "Juan";
persona.edad = 30;
System.out.println(persona.nombre);
System.out.println(persona.edad);
```

Este código sigue buenas o malas prácticas? MALAS POR QUE EXPONE SUS ATRIBUTOS. Y es cierto... pero por un mal diseño del lenguaje JAVA y su gramática.
Por qué eso es mala práctica?


```java
public class Persona {
    private String nombre;
    private int edad;

    public Persona(String nombre, int edad) {
        setNombre(nombre);
        setEdad(edad);
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
}
```
Esta es la que me dicen que es buena práctica (Y ES CIERTO EN JAVA)... pero por qué? POQUE ESTA MIERDA ESTA EXPONIENDO LOS ATRIBUTOS DE LA MISMA FORMA!

El problema es de mantenibilidad.

```java
// Día 1
public class Persona {
    public String nombre;
    public int edad;
}

// día 2 -100
Persona persona = new Persona();
persona.nombre = "Juan";
persona.edad = 30;
System.out.println(persona.nombre);
System.out.println(persona.edad);

// Día 101... Se me ocurre una idea feliz. Quiero controlar que la edad no pueda ser negativa. Dónde meto ese IF? Respuesta: NO HAY SITIO
// NO TENGO HUECO PARA PONERLO. En java solo puedo meter eso en funciones. Y NO LAS TENGO... por supuesto puedo crearlas:
public class Persona {
    public String nombre;
    private int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        setEdad(edad);
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        this.edad = edad;
    }
}
// día 102 sabeís que tengo? a cientos de personas , Kalasnikov en mano, buscándome por que su código NO COMPILA!
// El código de arriba ya no funciona... ahora debería de ser:
Persona persona = new Persona("Juan", 30);
System.out.println(persona.nombre);
System.out.println(persona.getEdad()); // Ya no vale .edad

```

Entonces en JAVA me dicen:
COMO EL LENGUAJE ES UNA MIERDA Y TIENE CARENCIAS GRAVES, Y NO HAY FORMA POSIBLE DE LIDIAR CON ESTOS ESCENARIOS A FUTURO, DESDE EL DIA 1 CREA GETTERS Y SETTERS PARA TODO... por si acaso. Esta es la buena práctica...

El truco es que esta parte se suele omitir:
        COMO EL LENGUAJE ES UNA MIERDA Y TIENE CARENCIAS GRAVES, Y NO HAY FORMA POSIBLE DE LIDIAR CON ESTOS ESCENARIOS A FUTURO.

En csharp este esta resuelto desde la creación del lenguaje: PROPERTIES

```csharp
// día 1:
public class Persona{
    public string Nombre { get; set; }
    public int Edad { get; set; }
}
// día 101
public class Persona {
    public string Nombre { get; set; }
    private int edad;
    public int Edad {
        get { return edad; }
        set { // VER 1
            if (value < 0) {
                throw new ArgumentException("La edad no puede ser negativa");
            }
            edad = value;
        }
    }
}

// Pero el uso es siempre el mismo:;
Persona persona = new Persona();
persona.Nombre = "Juan";
persona.Edad = 30; // Lo único es que desde el día 101, esto por DETRÁS accede a una función (1)
Console.WriteLine(persona.Nombre);
Console.WriteLine(persona.Edad);
```

Este concepto existe en JS, PYTHON, TS, CSHARP... en todos los lenguajes... menos en JAVA.

Es un tostón tener setters y getters vacios.
No por crearlos.. que cualquier editor lo hace en automático... Lo que no quiero es verlos en pantalla... que me obligan a hacer un huevo de scroll.
Este problema en JAVA AUN NO SE HA RESUELTO... Pero la industria ya le ha buscado solución: LOMBOK

```java
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Persona {
    private String nombre;
    private int edad;
}
```
Ya no los veo... Lenguaje declarativo: QUIERO GETTERS Y SETTERS PARA TODAS LAS PROPIEDADES.
Llegará un día que quizás en JAVA monten algo como las properties... TARDE!

Y esto ha pasado con una de las novedades de JAVA 15: LOS RECORDS (unque tiene su puntito)

Hay muchas veces que queremos montar objetos inmutables, especialmente util al crear DTOs o Value Objects.

```java
public class Persona {

    private final String nombre;
    private final int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

}
```

Este es el código tradicional para crear un objeto inmutable en JAVA. Cada propiedad es `final` y solo tiene un getter, sin setters.
Tiramos mucho de clases inmutables al crear código... son mucho más fáciles de controlar.
Es horrible la sintaxis... HORRIBLE!

Por eso aparecen los records:

```java
public record Persona(String nombre, int edad) {}
```

Eso compila al mismito byte-code que la clase tradicional con getters y final fields. Bueno.. casi... 
Para acceder a los campos de un record, usamos los métodos que se generan automáticamente con el mismo nombre que los campos:

```java
Persona persona = new Persona("Juan", 30);
System.out.println(persona.nombre()); // NOTESE LOS PARENTESIS DETRAS DEL NOMBRE DEL CAMPO ... EIN???? Es como un getter, pero sin la palabra get????
System.out.println(persona.edad());   // Estamos acostumbrados a esta sintaxis para acceder a un campo? NO.. o accedo al campo como campo, o accedo a una método que se llame getXXX? De repente se inventan una terminología que no es la estandar de la industria: Se llama como el campo, pero es un método.
```

Era una carencia grande del lenguaje... Era absurdo tener que escribir 10-50-100 líneas de código para algo que se puede resolver en una linea!
Pero LLEGÓ TARDE Y MAL!
MAL: Con una forma RARA y no ESTANDAR de acceder a los datos
TARDE: Ya llevamos muchos años creando clases INMUTABLES con otra sintaxis: LOMBOK


```java
@Value
public class Persona {
    String nombre;
    int edad;
}
// Eso si.. esto se usa de la forma en la que la industria está acostumbrada:

Persona persona = new Persona("Juan", 30);
System.out.println(persona.getNombre());
System.out.println(persona.getEdad());

```

En este sentido, de mejorar la MENTENIBILIDAD del código hay otro cambio que se añadió en JAVA 1.8: Los métodos default en interfaces.
Son métodos muy mal entendidos:

Puedo meter código en una interfaz? CLARO QUESI... desde java 1.8. Y de varias formas: con métodos default, o con métodos estáticos públicos (los privados llegaron en java 1.9.. otra cagada ... lo hicieron a medias).

```java
public interface Calculadora {

    public static int restar(int a, int b) {
        return a - b;
    }

    default int sumar(int a, int b) {
        return a + b; // Esto está fatal.
    }
}
```

En las interfaces NO DEBEMOS PONER Métodos default para implementar lógica. No SE HAN CREADO para tener un sitio donde poner código que quiero REUSAR entre las implementaciones. Ese hueco lleva cubierto desde que sale JAVA: CLASES ABSTRACTAS. Y es perfecto!

Los métodos estáticos SI TIENEN CIERTO SENTIDO... y son el APAÑO de java para otra de sus mierdas!
El objetivo principal de una clase es permitirme generar INSTANCIAS DE ELLA CON NEW!

```java
new Persona("Juan", 30);
```

Lo que pasa es que a veces tengo funciones que NO DEPENDEN DE LOS DATOS DE UNA INSTANCIA!
```java

public class LibreríaDeOperacionesMatemáticas{

    private LibreríaDeOperacionesMatemáticas() {
        // Evito que se puedan crear instancias de esta clase
    }

    public static int sumar(int a, int b) {
        return a + b;
    }

    public static int restar(int a, int b) {
        return a - b;
    }

    public static int multiplicar(int a, int b) {
        return a * b;
    }

}
```
EIN ??? Guarreo 100% CHAPUZA TRAS CHAPUZA!

Mira que facilito lo ponen en python, con lo simpleque es:
Un fichero donde tiro las funciones que no dependen de ninguna instancia y las puedo usar directamente:

```python
# Fichero: libreria_de_operaciones_matematicas.py
def sumar(a, b):
    return a + b

def restar(a, b):
    return a - b

def multiplicar(a, b):
    return a * b

# En otro fichero:
from libreria_de_operaciones_matematicas import sumar, restar
sumar(3, 4)
```


Buenos.. al menos en una interfaz mq quito parte de la morralla... las interfaces no son instanciables:
```java
public interface LibreríaDeOperacionesMatemáticas {

    public static int sumar(int a, int b) {
        return a + b;
    }

    public static int restar(int a, int b) {
        return a - b;
    }

    public static int multiplicar(int a, int b) {
        return a * b;
    }

}
```
Me evito el constructor privado y dejo claro que de esto no hay que hacer instancias... es una interfaz

Pero que son los defaults. No lo hemos respondido aún.

Pretenden cubrir otro tema:

```java
public interface Diccionario {

    String getIdioma();

    boolean existePalabra(@NonNull String palabra); // Devuelve true si la palabra existe en el diccionario, false en caso contrario.

    Optional<List<String>> getDefiniciones(@NonNull String palabra);

}
```

Qué tal nos quedó este código despues de los cambios? Contrato cerrado... pero con DEFECTOS GRAVES QUE NO VIMOS.
Y no los vimos.. porque inicialmente usábamos una implementación llamada DiccionarioDesdeicheros.
El día que empezamos a implementar (día 101) la clase DiccionarioDesdeBBDD vimos una cagada gorda:
Que le falta? que le pasa? Que puede devolver getDefiniciones si trabajo contra BBDD?
- Las definiciones
- Las no definiciones (si la palabra no existe en el diccionario)
- UN OSTION EN TO LOS MORROS si la BBDD no está operativa

Y eso está definido en el contrato? NO


```java
// Dia 101
public interface Diccionario {

    String getIdioma();

    boolean existePalabra(@NonNull String palabra); // Devuelve true si la palabra existe en el diccionario, false en caso contrario.

    Optional<List<String>> getDefiniciones(@NonNull String palabra) throws ExceptionDatoNoRecuperable;

}
// Día 102? Otra vez la oficina a todo el mundo Kalasnikov en mano que su códigono compila! No están tratando la exception
// Solución:

public interface Diccionario {

    String getIdioma();

    boolean existePalabra(@NonNull String palabra); // Devuelve true si la palabra existe en el diccionario, false en caso contrario.

    @Deprecated
    Optional<List<String>> getDefiniciones(@NonNull String palabra);

    default Optional<List<String>> getDefiniciones2(@NonNull String palabra) throws ExceptionDatoNoRecuperable {
        //return getDefiniciones(palabra);
        throw new NotImplementedException("getDefiniciones2 no está implementado");
        // A quién le va a dar este error? A nadie... si nadie usaba esta función. es nueva!
        // Pero que consigo? NO JODER A NADIE! QUE EL CODIGO DE LAS IMPLEMENTACIONES SIGA COMPILANDO!
    }

}

```

Los default en interfaces sirven para ofrecer una forma de ir evolucionando los contratos sin romper las implementaciones existentes.