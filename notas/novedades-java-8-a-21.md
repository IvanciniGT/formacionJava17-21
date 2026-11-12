# Novedades de Java 8 a Java 21

Un resumen de lo que trajo cada versión LTS (y las intermedias que llegaron a ellas), pensado para
consultar. En cada novedad: si la hemos usado en el proyecto del diccionario (y desde qué rama), o
si no ha salido en el curso.

- ✅ **la usamos**, en la rama que se indica (el código la marca con un comentario `// Java N: ...`)
- 💬 **la vimos** en clase, sin usarla en el proyecto
- ➖ **no ha salido** en el curso: para saber que existe

Las referencias son los JEP de OpenJDK (`https://openjdk.org/jeps/<número>`): la propuesta que trajo
cada cambio, con su motivación y sus ejemplos.

---

## Java 8 (2014) — programación funcional

La versión que más cambió la forma de escribir Java, junto con la 9 (módulos).

| Novedad | Para qué | | JEP |
|---|---|---|---|
| **Lambdas** | Funciones anónimas dentro de una expresión: `palabra -> palabra.length()` | ✅ `v1.0` | 126 |
| **Referencias a método** (`::`) | Una lambda que solo llama a un método: `String::trim`, `PalabraEncontrada::new` | ✅ `v1.0` | 126 |
| **Interfaces funcionales** (`java.util.function`) | El tipo de una lambda: `Function`, `Predicate`, `Supplier`, `Consumer`... | ✅ `v1.0` (pruebas) | 126 |
| **Streams** (`java.util.stream`) | El map-reduce de Java: `filter`, `map`, `sorted`, `collect`... sin bucles | ✅ `v1.0` | 107 |
| **`parallelStream`** | El mismo stream, repartido entre los núcleos de la máquina | ✅ `v3.1` (sugerencias) | 107 |
| **`Optional`** | Decir en la firma que un valor puede no estar, en vez de devolver `null` | ✅ `v1.0` | — |
| **Métodos `default` en interfaces** | Que un interfaz evolucione sin romper a quien ya lo implementa | ✅ `v1.2` | 126 |
| **Métodos estáticos en interfaces** | Funciones de utilidad sin una clase con constructor privado | ✅ `v1.0` (`FormatoDeFicheroDeDiccionario`) | 126 |
| **Métodos nuevos en colecciones** | `Map.computeIfAbsent`, `getOrDefault`, `forEach`, `removeIf`... | ✅ `v1.2` (`computeIfAbsent`) | — |
| **`java.time`** | Fechas que no dan pena: `LocalDate`, `LocalDateTime`, `Instant`, `Duration`, `Period`... | ✅ `v3.0` (`Instant`, en el cargador) | 150 |
| `CompletableFuture` | Programación asíncrona componiendo tareas | ➖ | 155 |
| `String.join`, `StringJoiner` | Unir textos con un separador | ✅ `v1.0` (`String.join`) | — |
| Anotaciones repetibles y de tipo | `@Foo @Foo`, anotaciones en cualquier uso de un tipo | ➖ | 120, 104 |
| `Base64` | Codificar y decodificar sin librerías externas | ➖ | 135 |
| Nashorn (JavaScript en la JVM) | Eliminado en Java 15 | ➖ | 174 |

## Java 9 (2017) y 10 (2018)

| Novedad | Para qué | | JEP |
|---|---|---|---|
| **Módulos (JPMS, proyecto Jigsaw)** | `module-info.java`: qué necesita un módulo (`requires`), qué deja ver (`exports`), qué servicios usa u ofrece (`uses` / `provides`). La propia plataforma se parte en módulos | ✅ `v1.0` | 261, 200 |
| **`ServiceLoader` con módulos** | Encontrar la implementación de un interfaz sin nombrarla: la que haya en el module path | ✅ `v1.0` (las factorías) | 261 |
| **`List.of` / `Set.of` / `Map.of`** | Colecciones inmutables en una línea | ✅ `v1.0` | 269 |
| **Métodos privados en interfaces** | Partir la lógica de los `default` y los estáticos sin exponerla | ✅ `v3.1` (`PalabrasSimilares`) | 213 |
| **`@Deprecated(forRemoval = true, since = ...)`** | No solo "no lo uses": "se va a quitar" | ✅ `v1.2` | 277 |
| **`InputStream.readAllBytes()`**, `transferTo` | Leer todo el contenido de una vez | ✅ `v1.0` | — |
| `Optional.ifPresentOrElse`, `or`, `stream` | Más formas de trabajar con un `Optional` | ➖ | — |
| `Stream.takeWhile`, `dropWhile`, `iterate` con condición | Cortar un stream según una condición | ➖ | — |
| `jshell` | Una consola para probar Java línea a línea | 💬 | 222 |
| `try` con recursos *effectively final* | `try (recurso)` sin redeclararlo | ➖ | 213 |
| `Process` API mejorada | Información y control de procesos del sistema | ➖ | 102 |
| **`var`** (Java 10) | El compilador deduce el tipo de una variable local | 💬 | 286 |
| **`List.copyOf` / `Set.copyOf` / `Map.copyOf`** (Java 10) | Copia inmutable de una colección (y si ya lo era, no copia) | ✅ `v1.0` | — |
| `Optional.orElseThrow()` sin argumentos (Java 10) | Como `get()`, pero diciendo lo que hace | ➖ | — |

## Java 11 (2018) — LTS

| Novedad | Para qué | | JEP |
|---|---|---|---|
| **`java.net.http.HttpClient`** | Cliente HTTP del JDK, síncrono y asíncrono, HTTP/2, con patrón builder | ✅ `v2.0` (servicio remoto) | 321 |
| **Métodos nuevos de `String`** | `isBlank`, `strip` (como `trim`, pero con todos los espacios Unicode), `lines`, `repeat` | ✅ `v1.0` | — |
| **`Files.readString` / `writeString`**, `Path.of` | Leer y escribir un fichero de texto en una línea | ✅ `v1.0` (`Path.of`), `v2.0` (`writeString`) | — |
| `Optional.isEmpty()` | Lo contrario de `isPresent()` | ✅ `v1.0` | — |
| `var` en los parámetros de una lambda | `(var a, var b) -> ...`, para poder anotarlos | ➖ | 323 |
| Ejecutar un `.java` sin compilarlo | `java Fichero.java` | ✅ `herramientas/GeneradorDeDiccionarios.java` | 330 |
| `Collection.toArray(IntFunction)` | `lista.toArray(String[]::new)` | ➖ | — |
| Se quitan Java EE y CORBA del JDK | `javax.xml.bind`, `javax.annotation`... pasan a ser librerías aparte | 💬 | 320 |

## Java 12 a 17 (2019–2021) — LTS: 17

| Novedad | Para qué | | JEP |
|---|---|---|---|
| **`switch` como expresión** (Java 14) | Devuelve un valor, con `case ... ->` y sin `break`; `yield` en los bloques | ✅ `v2.0` (controlador) | 361 |
| **Bloques de texto** (Java 15) | Strings de varias líneas con `"""`, sin `\n` ni `+` | ✅ `v1.0` | 378 |
| **`record`** (Java 16) | Clases inmutables de datos: constructor, accesores, `equals`, `hashCode` y `toString` generados. También locales, dentro de un método | ✅ `v1.0` (locales en `v3.1`) | 395 |
| **`instanceof` con patrón** (Java 16) | Comprobar el tipo y declarar la variable ya convertida: `if (o instanceof String s)` | 💬 (en el código, comentado como "así era antes") | 394 |
| **`Stream.toList()`** (Java 16) | Lista inmutable, sin `collect(Collectors.toList())` | ✅ `v1.0` | — |
| **Clases e interfaces selladas** (Java 17) | `sealed ... permits`: la lista cerrada de quién puede implementar algo. Contratos cerrados (Liskov) | ✅ `v1.0` (`ResultadoDeBusqueda`) | 409 |
| **`HexFormat`** (Java 17) | Bytes a hexadecimal y al revés | ✅ `v3.0` (la huella SHA-256 del cargador) | — |
| Mensajes de `NullPointerException` útiles (Java 14) | Dicen qué era `null`: `Cannot invoke "String.length()" because "palabra" is null` | ➖ | 358 |
| `Collectors.teeing` (Java 12) | Dos colectores sobre el mismo stream, y combinar sus resultados | ➖ | — |
| `String.indent`, `transform`, `formatted` (Java 12/15) | Más métodos de `String` | ➖ | — |
| `Stream.mapMulti` (Java 16) | Un elemento produce cero o varios, sin un stream intermedio | ➖ | — |
| Nuevos recolectores de basura: ZGC, Shenandoah | Pausas de milisegundos con montones enormes | ➖ | 377, 379 |
| `jpackage` (Java 16) | Instaladores nativos de una aplicación Java | ➖ | 392 |
| Encapsulación fuerte del JDK (Java 16/17) | Ya no se puede usar por reflexión el interior del JDK (`sun.misc.*`...) | ➖ | 396, 403 |
| Generadores de números aleatorios nuevos (Java 17) | `RandomGenerator` y sus algoritmos | ➖ | 356 |

## Java 18 a 21 (2022–2023) — LTS: 21

| Novedad | Para qué | | JEP |
|---|---|---|---|
| **`switch` con patrones** | `case PalabraEncontrada encontrada ->`: el `switch` sobre tipos, y **exhaustivo** sobre una sellada (sin `default`, y si aparece un caso nuevo, deja de compilar) | ✅ `v1.0` | 441 |
| **Patrones de `record`** (*record patterns*) | Desmontar un record en el propio patrón: `case PalabraEncontrada(String p, String i, List<Significado> s) ->` | ✅ `v1.0` | 440 |
| `case null` y guardas `when` | `case String s when s.isBlank() ->` | ➖ | 441 |
| **Hilos virtuales** | Hilos baratísimos que gestiona la JVM: millones a la vez. Un servidor que atiende cada petición en su hilo deja de quedarse sin hilos esperando a la BBDD o a la red. En Spring Boot: `spring.threads.virtual.enabled=true` | ➖ | 444 |
| **Colecciones secuenciadas** | `SequencedCollection`: `getFirst`, `getLast`, `addFirst`, `reversed`... en `List`, `Deque`, `LinkedHashSet`, `LinkedHashMap` | ➖ | 431 |
| UTF-8 por defecto (Java 18) | El juego de caracteres por defecto deja de depender del sistema operativo | 💬 (seguimos poniéndolo explícito) | 400 |
| Servidor web sencillo (Java 18) | `jwebserver`, para servir ficheros estáticos | ➖ | 408 |
| `@snippet` en Javadoc (Java 18) | Ejemplos de código en la documentación que se comprueban | ➖ | 413 |
| Generational ZGC | ZGC con generaciones: menos trabajo para el recolector | ➖ | 439 |
| Cifrado: KEM API | Encapsulación de claves, para criptografía post-cuántica | ➖ | 452 |
| En vista previa en la 21: plantillas de texto, clases sin nombre y `main` de instancia, concurrencia estructurada, valores con ámbito | Llegan (o cambian) en versiones posteriores | ➖ | 430, 445, 453, 446 |

---

## Lo que se ve en cada rama del proyecto

| Rama | Java que aparece por primera vez |
|---|---|
| `v1.0-consola-ficheros` | Lambdas, referencias a método, streams, `Optional`, estáticos en interfaces, módulos y `ServiceLoader`, `List.of`, `List.copyOf`, `String` (Java 11), `Path.of`, `readAllBytes`, `String.join`, bloques de texto, `record`, `Stream.toList()`, `sealed`, `switch` con patrones y *record patterns* |
| `v1.1-control-de-configuracion` | Ninguna: es control de configuración |
| `v1.2-repositorio-que-avisa-de-errores` | Métodos `default`, `@Deprecated(forRemoval)`, `Map.computeIfAbsent` |
| `v2.0-servidor-central` | `HttpClient`, `switch` como expresión, `Files.writeString`; y Spring: inversión de control, inyección de dependencias, las factorías como `@Bean` |
| `v3.0-bbdd` | `HexFormat`, `java.time.Instant`; y Spring Data JPA, transacciones, autoconfiguración |
| `v3.1-sugerencias` | `parallelStream`, `record` local, métodos privados en interfaces |
| `v4.0-frontal-web` | (TypeScript: uniones discriminadas, el `sealed` de TypeScript) |

En el README de `proyectos/diccionario` (en cualquier rama), la tabla "Novedades de Java 8+ en este
código" lleva a la línea exacta donde se usa cada una.

## Para seguir

Lo que más merece la pena probar de lo que no ha salido:

1. **Hilos virtuales** (Java 21). En el servidor del diccionario es una línea en
   `application.properties`: `spring.threads.virtual.enabled=true`. Y a mano:
   `Executors.newVirtualThreadPerTaskExecutor()`.
2. **Colecciones secuenciadas** (Java 21): `lista.getFirst()` y `lista.reversed()` en vez de
   `lista.get(0)` y `Collections.reverse`.
3. **`case ... when`** en el `switch` con patrones (Java 21): condiciones dentro de cada caso.
4. **`var`** (Java 10): cómodo cuando el tipo ya se lee a la derecha
   (`var palabras = new HashMap<String, List<Significado>>()`), y oscuro cuando no.
5. **`CompletableFuture`** (Java 8) y `HttpClient.sendAsync` (Java 11): la versión asíncrona de lo
   que hace el servicio remoto.
