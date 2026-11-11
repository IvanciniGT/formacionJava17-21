
# Principio de sustitución de Liskov

Que dice: Si S es un subtipo de T, entonces los objetos de tipo T en un programa pueden ser reemplazados por objetos de tipo S sin alterar las propiedades deseables del programa (corrección, tarea realizada, etc.).

En otras palabras, los subtipos deben ser sustituibles por sus tipos base sin que el comportamiento del programa se vea afectado.

Y tiene una implicación enorme: LOS CONTRATOS DEBEN PODER CERRARSE! Con letra chiquita!

> Ejemplo nuevo:

Quiero montar un servicio REST (HTTP) que permita a una persona hacer una petición para obtener los significados de una palabra en un idioma.

URL (endpoint)
 GET     /idioma/{idioma}/{palabra}                             http://miservidor:puerto/idioma/es/pepinillo -> {JSON}
 GET     /significados/{idioma}/{palabra}
 GET     /significados?idioma={idioma}&palabra={palabra}

Definiré una función en JAVA que atienda a esa URL. es decir, cuando alguien llame a esa URL se ejecutará esa función JAVA y devolverá la respuesta correspondiente en formato JSON.
Esto es arquitecturas orientadas a componentes deesacoplados, este tipo de componente (el que define estas funciones/endpoints) recibe un nombre: Controlador.

El controlador solo EXPONE las funciones definadas en otro tipo de compoente mediante un determinado PROTOCOLO (en este caso HTTP/REST).
Puedo tener en mi sistema múltiples controladores para exponer el mismo SERVICIO (que es otro tipo de componente). Podría tener un controlador REST y un controlador SOAP.

```java
import lombok.NonNull;
public interface SignificadosService {
    Optional<List<String>> consultarSignificados(@NonNull String idioma, @NonNull String palabra) throws SignificadosServiceException;
}
```

Lo tenemos? NO.... nos falta algo a nivel FUNCIONAL!
Hay información que no estamos aportando a quién llame a esta función.
- Si paso : IDIOMA=ESPAÑOL y PALABRA=PEPINILLO
  Devuelve un Optional RELLENO con una List<String> con las definiciones de la palabra "pepinillo" en español.
- Si paso : IDIOMA=ESPAÑOL y PALABRA=ARCHILOCOCO
  Devuelve un Optional VACÍO.
- Si paso : IDIOMA=ELFICO y PALABRA=ARCHILOCOCO  // Y no tenemos idioma ELFICO
  Devuelve un Optional VACÍO.

El problema es claro.. Cuando alguien me llama a esta función y recibve a cambio un Optional vacío, no sabe si 
- La palabra no existe
- El idioma no está soportado por el servicio.

Cómo lo resolvemos? Definimos nuestro propio Objeto de Respuesta. Los Optional están muy bien.. si quiero distinguir entre 2 casos.
Pero en nuestro escenario queremos distinguir claramente entre 3 casos:
1. La palabra existe y se encuentran definiciones. -> Optional relleno: List<String>
2. La palabra no existe.                           -> Optional vacio  
3. El idioma no está soportado por el servicio.

```java
import lombok.NonNull;
public interface SignificadosService {
    ResultadoConsultaSignificados consultarSignificados(@NonNull String idioma, @NonNull String palabra) throws SignificadosServiceException;
}

/**
 * Resultado de la consulta de significados de una palabra en un determinado idioma.
 * @param idioma El idioma en el que se realiza la consulta.
 * @param palabra La palabra cuya definición se consulta.
 * @param palabraExiste Indica si la palabra existe en el idioma especificado.
 * @param idiomaSoportado Indica si el idioma está soportado por el servicio.
 * @param definiciones Lista de definiciones de la palabra, si exist la palabra en el idioma especificado. Y el idioma está soportado. Si no lleva vacío (null)
 */
public record ResultadoConsultaSignificados (
    String idioma,
    String palabra,
    boolean palabraExiste,
    boolean idiomaSoportado,
    List<String> definiciones 
) {

    // Como no es bueno devolver null, usamos Optional para las definiciones.
    public Optional<List<String>> definiciones() {
        return Optional.ofNullable(definiciones);
    }
}
// Pero sería un poco chapuza. Sobre todo en el USO...

ResultadoConsultaSignificados resultado = service.consultarSignificados("ESPAÑOL", "PEPINILLO");
if (resultado.palabraExiste() ) {
    System.out.println("Definiciones: " + resultado.definiciones());
} else if (!resultado.idiomaSoportado()) {
    System.out.println("Idioma no soportado" + resultado.idioma());
} else {
    System.out.println("La palabra no existe" + resultado.palabra() + " en el idioma " + resultado.idioma());
}
// Tengo que estar mirando qué campos están activos (tienen datos/sentido) en base a los valores de palabraExiste e idiomaSoportado para saber el resultado de la consulta.
// Donde lo miro? En la documentación del servicio, para saber qué significan los campos palabraExiste e idiomaSoportad??

// En lugar de esto tiene más sentido otra cosa: De hecho.. eso se caga en otro de los principio SOLID
// I: Interface Segregation Principle (Principio de Segregación de Interfaces)
// Que dice: "Los clientes no deberían verse obligados a depender de interfaces que no utilizan." 
// Más claro: No devuelvas algo que tiene Métodos que NO SIRVENM EN CIERTOS ESCENARIOS DE USO... Devuelve algo que tenga SOLO los métodos que son relevantes para ese escenario.

// Alternativa decente:

public interface ResultadoConsultaSignificados {
    String getIdioma();
    String getPalabra();
}
@Value
public class ResultadoConsultaCorrecto implements ResultadoConsultaSignificados {
    String idioma;
    String palabra;
    List<String> definiciones;
}
@Value
public class ResultadoConsultaSignificadosIdiomaNoSoportado implements ResultadoConsultaSignificados {
    String idioma;
    String palabra;
}
@Value
public class ResultadoConsultaSignificadosPalabraNoExiste implements ResultadoConsultaSignificados {
    String idioma;
    String palabra;
}

// Cómo lo uso ahora: instance of
ResultadoConsultaSignificados resultado = service.consultarSignificados("ESPAÑOL", "PEPINILLO");
if (resultado instanceof ResultadoConsultaCorrecto correcto) {   // TACHAN!!! Nueva sintaxis para el instanceof con patrón de asignación
    System.out.println("Definiciones: " + correcto.getDefiniciones());
} else if (resultado instanceof ResultadoConsultaSignificadosIdiomaNoSoportado noSoportado) {
    System.out.println("Idioma no soportado: " + noSoportado.getIdioma());
} else if (resultado instanceof ResultadoConsultaSignificadosPalabraNoExiste noExistePalabra) {
    System.out.println("La palabra no existe: " + noExistePalabra.getPalabra() + " en el idioma " + noExistePalabra.getIdioma());
}
// Antiguamente necesitábamos hacer casteos manuales:
// if (resultado instanceof ResultadoConsultaCorrecto) {
//     ResultadoConsultaCorrecto correcto = (ResultadoConsultaCorrecto) resultado;
//     System.out.println("Definiciones: " + correcto.getDefiniciones());
// } else if (resultado instanceof ResultadoConsultaSignificadosIdiomaNoSoportado) {
//     ResultadoConsultaSignificadosIdiomaNoSoportado noSoportado = (ResultadoConsultaSignificadosIdiomaNoSoportado) resultado;
//     System.out.println("Idioma no soportado: " + noSoportado.getIdioma());
// } else if (resultado instanceof ResultadoConsultaSignificadosPalabraNoExiste) {
//     ResultadoConsultaSignificadosPalabraNoExiste noExistePalabra = (ResultadoConsultaSignificadosPalabraNoExiste) resultado;
//     System.out.println("La palabra no existe: " + noExistePalabra.getPalabra() + " en el idioma " + noExistePalabra.getIdioma());
// }
```


```java
import lombok.NonNull;
public interface SignificadosService {
    ResultadoConsultaSignificados consultarSignificados(@NonNull String idioma, @NonNull String palabra) throws SignificadosServiceException;
}
```

Qué tal ahora? Pues la hemos cagado al hacer esto! Acabamos de pisar de nuevo el principio de sustitución de Liskov.
Qué devuelve ahora nuestra función? Un ResultadoConsultaSignificados... PERO ES UNA INTERFAZ, NO SABEMOS QUÉ IMPLEMENTACIÓN CONCRETA NOS VA A DEVOLVER.
Eso lo decidirá la implementación concreta de SignificadosService que estemos usando.

Dicho de otra forma:

```java
public class SignificadosServiceImpl implements SignificadosService {
    @Override
    public ResultadoConsultaSignificados consultarSignificados(@NonNull String idioma, @NonNull String palabra) throws SignificadosServiceException {
        try {
        // Mi lógica
        } catch (Exception e) {
            return new ErrorAlAccederABBDD(idioma, palabra, e.getMessage());
        }
    }

    @Value
    public class ErrorAlAccederABBDD implements ResultadoConsultaSignificados { //DTO=Data Transfer Object .. No hay lógica
        String idioma;
        String palabra;
        String mensaje;
    }
}
```
La pregunta es: PUEDE ALGUIEN PERTURBADO MONTAR ESTA IMPLEMENTACION?
Y si escribo ese código, lo anterior funcionaría? DE OTRA FORMA... diferente. De hecho el error no se mostraía por ningún sitio.

```java
// Cómo lo uso ahora: instance of
ResultadoConsultaSignificados resultado = service.consultarSignificados("ESPAÑOL", "PEPINILLO");
if (resultado instanceof ResultadoConsultaCorrecto correcto) {   // TACHAN!!! Nueva sintaxis para el instanceof con patrón de asignación
    System.out.println("Definiciones: " + correcto.getDefiniciones());
} else if (resultado instanceof ResultadoConsultaSignificadosIdiomaNoSoportado noSoportado) {
    System.out.println("Idioma no soportado: " + noSoportado.getIdioma());
} else if (resultado instanceof ResultadoConsultaSignificadosPalabraNoExiste noExistePalabra) {
    System.out.println("La palabra no existe: " + noExistePalabra.getPalabra() + " en el idioma " + noExistePalabra.getIdioma());
}
```

VAYA MIERDA DE CONTRATRO que permite que una implementación pueda devolver cualquier cosa que implemente ResultadoConsultaSignificados, incluso errores que no se muestran por ningún sitio.

El contrato no está CERRADO... acabamos de dejarle un agujero ENORME!!
Todo ha venido por una carencia de JAVA: JAVA NO SOPORTA ALTERNANCIA DE TIPOS EN RETORNOS de funciones... otros lenguajes si: VEASE TYPESCRIPT!

```ts
def consultarSignificados(idioma: String, palabra: String): ResultadoOK | ResultadoKO_IdiomaNoSoportado | ResultadoKO_PalabraNoExiste {
    // Mi lógica
}
```
En JAVA no se puede... y tengo que definir una INTERFAZ o CLASE COMUN que sirva como tipo de retorno para todas las posibles implementaciones correctas o de error.

Esta interfaz:  ResultadoConsultaSignificados     solo existe porque en JAVA no hay forma de retornar múltiples tipos alternativos desde una función.
OK... te compro la chapuza... ME VEO FORZADO A CREAR UN TIPO COMUN... pero... al hacerlo me aparece un problemón: EL CONTRATO QUEDA SIN CERRAR.
Quien implemente puede crear sus propias nuevas implementaciones de la INTERFAZ: cualquier clase que implemente ResultadoConsultaSignificados será válida como retorno, incluso si representa un error que no estaba contemplado originalmente.

RESULTA QUE EL CONTRATO QUEDA ABIERTO...

Solución: JAVA 17: SELLAR LA INTERFAZ

```java
public sealed interface ResultadoConsultaSignificados permits 
    ResultadoConsultaCorrecto, 
    ResultadoConsultaSignificadosIdiomaNoSoportado, 
    ResultadoConsultaSignificadosPalabraNoExiste {
}
```

Esto se puede hacer con interfaces, y también con clases selladas.
Al sellar una interfaz o una clase, explícitamente DIGO cuáles son las implementaciones/subtipos permitidos.
El compoilador dará error si alguien intenta crear una nueva implementación de ResultadoConsultaSignificados que no esté listada en la cláusula `permits`. 
Esto cierra el contrato y asegura que solo las implementaciones permitidas puedan ser retornadas por la función.

YA nadie podrá crear nuevas implementaciones de ResultadoConsultaSignificados que no estén explícitamente permitidas en la cláusula `permits`.

Si os daís cuenta... de alguna forma es otra ñapa, sobre ñapa... si tuvieramos alternancia de tipos en los retornos, como en TypeScript, no necesitaríamos sellar la interfaz para cerrar el contrato. No necesaitaría ni tener una interfaz común solo para poder retornar múltiples tipos.

Y esto de los sealed classes e interfaces... tiene otra cosa aparejada: Los switch por patrones sobre estos tipos sellados se vuelven exhaustivos. 
Otra nueva funcionalidad (java 21):

El código anterior que usaba la función:

```java
ResultadoConsultaSignificados resultado = service.consultarSignificados("ESPAÑOL", "PEPINILLO");
if (resultado instanceof ResultadoConsultaCorrecto correcto) {   // TACHAN!!! Nueva sintaxis para el instanceof con patrón de asignación
    System.out.println("Definiciones: " + correcto.getDefiniciones());
} else if (resultado instanceof ResultadoConsultaSignificadosIdiomaNoSoportado noSoportado) {
    System.out.println("Idioma no soportado: " + noSoportado.getIdioma());
} else if (resultado instanceof ResultadoConsultaSignificadosPalabraNoExiste noExistePalabra) {
    System.out.println("La palabra no existe: " + noExistePalabra.getPalabra() + " en el idioma " + noExistePalabra.getIdioma());
}
```

ahora podría transformarlo en :

```java
ResultadoConsultaSignificados resultado = service.consultarSignificados("ESPAÑOL", "PEPINILLO");
switch (resultado) {
    case ResultadoConsultaCorrecto correcto -> System.out.println("Definiciones: " + correcto.getDefiniciones());
    case ResultadoConsultaSignificadosIdiomaNoSoportado noSoportado -> System.out.println("Idioma no soportado: " + noSoportado.getIdioma());
    case ResultadoConsultaSignificadosPalabraNoExiste noExistePalabra -> System.out.println("La palabra no existe: " + noExistePalabra.getPalabra() + " en el idioma " + noExistePalabra.getIdioma());
}
```

NOTA: LA FLECHA en esta sintaxis no es un operador lambda, sino que indica la acción a realizar para cada caso del switch.
Si el case necesitase varias lineas de código:

```java
switch (resultado) {
    case ResultadoConsultaCorrecto correcto -> {
        System.out.println("Definiciones: " + correcto.getDefiniciones());
        // Otras acciones que necesitemos realizar para este caso
    }
    case ResultadoConsultaSignificadosIdiomaNoSoportado noSoportado -> {
        System.out.println("Idioma no soportado: " + noSoportado.getIdioma());
        // Otras acciones que necesitemos realizar para este caso
    }
    case ResultadoConsultaSignificadosPalabraNoExiste noExistePalabra -> {
        System.out.println("La palabra no existe: " + noExistePalabra.getPalabra() + " en el idioma " + noExistePalabra.getIdioma());
        // Otras acciones que necesitemos realizar para este caso
    }
}
```

Otra cosa importante: ESTE SWITCH ES EXHAUSTIVO, lo que significa que TIENE QUE cubrir todos los posibles tipos de resultados (DEL PERMITS). Si no se cubre algún tipo, el compilador nos dará un error.
Esto me asegura que no me olvidaré de manejar ningún caso posible, evitando errores en tiempo de ejecución y haciendo que el código sea más seguro y fácil de mantener.
Y me asegura que si el día de mañana alguien mete un nuevo tipo de resultado en el `permits`, el compilador me obligará a actualizar este switch para manejarlo, evitando que se me pase por alto algún caso.

---

Para poder hacer contratos decentes y estables (respetando liskov y otros principios SOLID), tenemos:
- Optional<T>
- default methods en interfaces
- sealed classes y interfaces (con `permits`)
- switch con pattern matching (exhaustivo)

Cuando trabajábamos con monolitos, alos contratos no les dábamos tanta importancia, ya que el código estaba más centralizado y era más fácil de controlar.
En arquitecturas con componentes desacoplados (como por ejemplo arquitecturas de microservicios), los contratos entre componentes cobran mucha más importancia, ya que cada componente puede evolucionar de manera independiente y necesitamos asegurarnos de que las interacciones entre ellos sean correctas y estables.

---

# MODULOS!

Posiblemente es el mayor cambio que hay en JAVA, (JAVA 9) junto con el soporte de programación funcional (Java 1.8).
Se conoce como el proyecto JIGSAW y su objetivo principal es modularizar la plataforma Java, permitiendo dividirla en módulos independientes y gestionables.

Pero antes de meterme con ello... el problema gordo!


---

> Volvamos a nuestra Librería de diccionarios!

Imaginad que queremos hacer una app de consola (linea de comandos) que permita encontrar los significados de palabras en diferentes idiomas.

c:\> java -jar DiccionarioAppConsola.jar IDIOMA PALABRA

Por ejemplo:
c:\> java -jar DiccionarioAppConsola.jar es casa
    La palabra casa existe en el diccionario de español.
    Sus significados son:
        - Lugar donde se habita.
        - Vivienda familiar.

c:\> java -jar DiccionarioAppConsola.jar es archilococo
    La palabra archilococo no existe en el diccionario de español.

c:\> java -jar DiccionarioAppConsola.jar elf eldeanor
    Lo siento, pero no tenemos diccionario para el idioma élfico.

En principio, las palabras y sus significados las tendremos en ficheros de texto planos.... uno por idioma.

Pregunta... Cuántos proyectos monto para esta aplicación?
En java, cuántos archivos pom.xml de maven voy a tener para esta aplicación?
Cuántos repositorios de GIT vamos a crear para esto?
Si trabajase en csharp: cuántos proyectos tiene que tener esta solución?

- Interfaz
- Gestión de diccionarios/palabras/significados
- Repositorio / DATOS

---

# Qué problema tiene este sistema tal y como está diseñado? (tal y como lo estamos planteando?)

El problema grandes es que coste de OPERACION de este sistema es una locura!
Muchas veces los desarrolladores nos centramos en el coste de desarrollo.
Cada vez menos... y vamos entendiendo un poco más acerca del CICLO DE VIDA de un producto de software.
Lo que a una empresa le interesa (y por ende a nosotros) no es bajar el coste de desarrollo.
Lo que interesa bajar es el TCO (Total Cost of Ownership), es decir, el coste total de propiedad del sistema a lo largo de su ciclo de vida.
Lo cual incluye la OPERACION del sistema, es decir, los costes asociados a mantenerlo funcionando correctamente, como la gestión de incidencias, actualizaciones, soporte, infraestructura, etc.

Este sistema se instala en local, en cada máquina donde se quiera usar.... de mis empleados.. Y esto tiene problemas GRAVISIMOS en su operación:
- Las instalaciones se complican... No es una centralizada... Tengo un huevo... y cada vez que salga una actualización, tengo que ir máquina por máquina aplicándola.
  Con usuarios que no son expertos. (YO SOY EXPERTO.. los de sistemas son expertos... los usuarios de finanzas, o de marketing.,. o del dpto que sea no son expertos en informática) 
- Puede ser que distintos usuarios tengan instalada distintas versiones del sistema, lo que complica:
  -  la gestión de incidencias y soporte.
  -  distintos usuarios podrían estar recibiendo distintas respuestas o comportamientos del sistema debido a las versiones diferentes.
- Cualquier cambio implica redistribuir el sistema entero.

- Si en lugar de tener palabras en ficheros, las tengo en una BBDD centralizada:
 - Cuando vengan nuevas palabras o nuevos idiomas, tendré que reinstalar algo en los clientes? NO debería
- Si en lugar de una interfaz consola, tuviera una interfaz web, cuando quiera cambiar algo de la interfaz (por ejemplo, una nueva funcionalidad que he añadido - mostrar ejemplos de uso de palabras- ) tedría que reinstalar algo en las máquinas de los usuarios? NO debería

Hay muchas decisiones que podría tomar para bajar ENORMEMENTE el coste de operación de este sistema... aún a expensas de subir un poco el coste de desarrollo inicial.

Lo importante no es la decisión que tome hoy. En cuanto a si quiero o no BBDD, si quiero o no interfaz web... POR QUE ESA DECISION, SEA CUAL SEA, EN EL FUTURO CAMBIARÁ!

Lo que necesito es asegurarme que cuando lleguen esos cambios, el sistema pueda adaptarse sin que el coste de desarrollo se dispare.
Y aquí entran varias decisiones:
- Buscar una forma de trabajo que me haga poder responder de forma ágil a los cambios que se vayan planteando
- Diseñar una arquitectura que sea flexible y modular, con componentes desacoplados, de manera que los cambios en una parte del sistema no afecten al resto y se puedan implementar de forma independiente.

Esto son las cosas que hemos aprendido a lo largo de los años/décadas de crear software.
Claramente aquí no tenemos un único proyecto... aunque podría hacerlo todo en un único proyecto = MONOLITO... pero ya sabemos (por experiencia) que los monolitos tienden a volverse difíciles de mantener y evolucionar con el tiempo. Por eso, es preferible diseñar sistemas modulares y desacoplados desde el principio. Aún asumiendo que el coste inicial del desarrollo será más alto que el de montar un sistema monolítico. A largo plazo se gana en flexibilidad y facilidad de mantenimiento.

Vamos a diseñar el sistema con varios COMPONENTES... MODULOS... PROYECTOS... Desacoplados entre si... Es decir, que pueda cambiar uno de ellos en el futuro sin que tenga impact en los demás. Esto nos permitirá evolucionar el sistema de manera más ágil y con un coste de mantenimiento más bajo.

Así lo hacen por ejemplo en la índustria de la automoción. Un coche no es un monolito.. tiene muchos componentes:
- Alternador
- Bateria
- Bujias
- Frenos
- Neumáticos
- Carrocería
- Motor
- Transmisión

Y puedo ir cambiando esos componentes por separado sin tener que rediseñar todo el coche. Esto es exactamente lo que queremos lograr en nuestro sistema de software: componentes modulares y desacoplados que puedan evolucionar de manera independiente. No estamos pidiendo nada especial... pero esto es justo lo contrario de un monolito.

Componentes:                                Responsabilidad
- Interfaz de usuario                           Gestionar la interacción con el usuario, mostrar información y recibir entradas.
- RepositorioDeDiccioanrios                     Gestionar el almacenamiento y recuperación de diccionario / palabra / significados
- ServicioDeDiccionarios                        Gestionar la lógica de negocio relacionada con las búsquedas de palabras

Qué son estos componentes? A nivel de código? Clases / Interfaces / Paquetes(Namespaces) con clases/interfaces

Un paquete es solo una forma de organizar clases/interfaces...
El tema es, qué meto en esos paquete? Clases o Interfaces? Neecesito interfaces e implementaciones. Por qué? Que me dan las interfaces? CONTRATO .. para qué lo quiero?
Para poder cambiar la implementación de un componente sin afectar a los demás. Contrato -> API (Application Programming Interface) es el conjunto de interfaces /clases simples que definen cómo interactuar con el componente sin exponer los detalles internos de su implementación.

Un coch, sus componentes se especifican, por contrato : Mi coche necesita:
- Una bateria Varta modelo XJK009i <- Esto sería un desastre.. y si mañana quiero cambiar la batería? Solo puedo poner esa?
- Realmente la bateria de mi coche se define vía una ESPECIFICACION, CONTRATO, API:
  - Mi coche necesita una bateria de 12v, 72Ah, de tamaño A.
  - Y al hacerlo, cualquier batería que cumpla con esas especificaciones será compatible con mi coche. Esto me da flexibilidad para cambiar de proveedor o modelo sin tener que rediseñar todo el sistema.

Para esto quiero contratos: APIS

Ahora bien.. un coche se monta/ensambla con contratos? con especificaciones? NO... con implementaciones.

Entonces necesitamos definir por un lado las especificaciones o contratos (interfaces) que describen qué necesita cada componente,
y por otro lado las implementaciones concretas que cumplen con esos contratos.

Es decir: 
- Interfaz de usuario API
- InterfazDeUsuarioDeConsola
- RepositorioDeDiccioanrios API
- RepositorioDeDiccioanriosEnFicheros
- ServicioDeDiccionarios API
- ServicioDeDiccionariosPorDefecto

Pero falta una cosa... Nos falta un componente, cuál? El coche! La aplicación

- AplicacionConsola

Esas cosas deben poder evolucionar por separado? Tiene sentido? SI... pues deben ser a todos los efectos PROYECTOS independientes. Cada componente (Interfaz de usuario, RepositorioDeDiccionarios, ServicioDeDiccionarios, AplicacionConsola) debe ser un proyecto separado que pueda evolucionar sin afectar a los demás.

Eso si, empieza a haber dependencias entre ellos:
                                                  Dependencia (que a nivel de código se traduce en un import - using)
                                                      v
                    Interfaz de usuario API          <-     InterfazDeUsuarioDeConsola                    <-
                    RepositorioDeDiccioanrios API    <-     RepositorioDeDiccioanriosEnFicheros           <-      AplicacionConsola
                    ServicioDeDiccionarios API       <-     ServicioDeDiccionariosPorDefecto              <-

Por donde empezamos? Por las APIs

```java
//RepositorioDeDiccioanrios API // ENTIDAD
package com.curso.diccioanrios.api;

import java.util.List;
import java.util.Optional;

public interface Diccionario {                                                      // El equivalente a un Diccionario FISICO en papel
    String getIdioma();
    boolean existe(String palabra) throws Exception;
    Optional<List<String>> getSignificados(String palabra) throws Exception;
}

public interface SuministradorDeDiccionarios {                                      // El equivalente a la tienda de la esquina (Librería)
    boolean tienesDiccionarioDe(String idioma) throws Exception;
    Optional<Diccionario> getDiccionario(String idioma) throws Exception;
}
```
Este es un proyecto.. que se compilay empaqueta de forma independiente al rto de componentes.. Esto generará un archivo             diccionarios-api.jar

Ahora que tengo contrato, puedo hacer una implementación... por ejemplo buscando los diccionarios y palabras en FICHEROS DE TEXTO:

```java
//RepositorioDeDiccioanrios En Ficheros
package com.curso.diccioanrios.enficheros;

import com.curso.diccioanrios.api.Diccionario;
import com.curso.diccioanrios.api.SuministradorDeDiccionarios;

import java.util.List;
import java.util.Optional;

public class DiccionarioEnFicheros implements Diccionario {
    private final String idioma;

    public DiccionarioEnFicheros(String idioma) {
        this.idioma = idioma;
    }

    @Override
    public String getIdioma() {
        return idioma;
    }

    @Override
    public boolean existe(String palabra) throws Exception {
        // Implementación de búsqueda en ficheros. Aquí es donde iría el código necesario
        return false;
    }

    @Override
    public Optional<List<String>> getSignificados(String palabra) throws Exception {
        // Implementación de búsqueda en ficheros. Aquí es donde iría el código necesario para obtener los significados de la palabra.
        return Optional.empty();
    }
}

public class SuministradorDeDiccionariosEnFicheros implements SuministradorDeDiccionarios {
    @Override
    public boolean tienesDiccionarioDe(String idioma) throws Exception {
        // Implementación de búsqueda en ficheros
        return false;
    }

    @Override
    public Optional<Diccionario> getDiccionario(String idioma) throws Exception {
        // Implementación de búsqueda en ficheros
        return Optional.of(new DiccionarioEnFicheros(idioma));
    }
}

package com.curso.diccioanrios.enficheros.utils;

public class UtilidadesParaTrabajarConFicheros {    // Al ponerla pública (copsa necesaria para que SuministradorDeDiccionariosEnFicheros pueda usarla)
                                                    // Acabo de abrirla a que realmente CUALQUIER PERSONA DEL MUNDO PUEDA USARLA
                                                    // Qué modificador hay en JAVA poara ello? Para que esto sea PUBLICO DENTRO DE MI PROYECTO, pero no fuera de él? 
                                                    // NO EXISTE EN JAVA
                                                    // Otros lenguajes si lo tienen (CSHARP: internal) PROBLEMON! Y esto es uno de los problemas que se atacan con los módulos en Java 9+
    // Aquí irían métodos estáticos de utilidad para trabajar con fichros, por ejemplo:
    // - leerFichero
    // - listarFicherosEnDirectorio
}
```

Proyecto acabado... con su propio .jar    ->                                        diccionarios-en-ficheros.jar

# API del servicio

```java
package com.curso.servicio.api;

public interface ServicioDeDiccionarios {

    ResultadoBusqueda buscarSignificados(String palabra, String idioma) throws Exception;

}
// interfaz ResultadoBusqueda, con 3 implemenatciones: PalabraEncontrada, PalabraNoEncontrada, IdiomaNoSoportado
```

Y esto, junto con el sealed interface ResultadoBusqueda, cierra este API ->             servicio-api.jar

# Implementación del servicio

```java
package com.curso.servicio.impl;

import com.curso.servicio.api.ServicioDeDiccionarios;
import com.curso.servicio.api.ResultadoBusqueda;
import com.curso.diccionarios.api.SuministradorDeDiccionarios;
import com.curso.diccionarios.api.Diccionario;
import com.curso.servicio.api.PalabraEncontrada;
import com.curso.servicio.api.PalabraNoEncontrada;
import com.curso.servicio.api.IdiomaNoSoportado;
//import com.curso.diccionarios.enficheros.utils.UtilidadesParaTrabajarConFicheros; // Esto es posible hacerlo? Está permitido? SI
// Y QUIERO PERMITIRLO? NO... esta clase , yo oh, creador del proyecto: DiccionariosEnFicheros, no quiero que la use nadie.. Es mia.
// Es más NADIE en su sano juicio, que no sea  (Oh! creador del proyecto: DiccionariosEnFicheros) debería usarla... Es mi clase y puedo cambiarla cuando me de la real gana.
// Y si alguien la usa, SE ATA A ELLA...y si yo la cambio, les jodo... Y QUIERO LIBERTAD PARA CAMBIARLA.. Y ELLOS (Oh creadores de otros compoenntes) QUIEREN NO DEPENDER DE MI
import java.util.ServiceLoader; // Esto es java 1.7... pero su explendor es en Java 1.9, con el sistema de módulos y el ServiceLoader mejorado.
//import com.curso.diccionarios.enficheros.SuministradorDeDiccionariosEnFicheros; // Y esta linea acaba de matar el proyecto = MONOLITO!
// Esta linea es distinta a los otros imports.. en qué? Es una clase (implementación) concreta, mientras que los otros son interfaces del API.
// De hecho, acabo de pisotear otro principio SOLID: Dependency Inversion Principle (DIP)

public class ServicioDeDiccionariosImpl implements ServicioDeDiccionarios {
    @Override
    public ResultadoBusqueda buscarSignificados(String palabra, String idioma/*, SuministradorDeDiccionarios suministrador*/) throws Exception {
        SuministradorDeDiccionarios suministrador = ServiceLoader.load(SuministradorDeDiccionarios.class).findFirst().orElseThrow();
                                                    //new SuministradorDeDiccionariosEnFicheros();
        if(suministrador.tienesDiccionarioDe(idioma)) {
            Diccionario diccionario = suministrador.getDiccionario(idioma).get(); // Esta excepción se debería producir en algún caso? Si hay un bug.
            if(diccionario.existe(palabra)) {
                return new PalabraEncontrada(idioma, palabra,diccionario.getSignificados(palabra).get());
            } else {
                return new PalabraNoEncontrada(idioma, palabra);
            }
        }else{
            return new IdiomaNoSoportado(idioma);

        }
    }
}
```


En cualquiera de los casos: ServiceLocator o Inyección de Dependencias.... hay un problema adicional!

Esto genera su propio jar       -> servicio-impl.jar
Salvo por una cosita... y es que ese código aún no compila!

## Principio de inversión de la dependencia (Dependency Inversion Principle - DIP)

Un componente de alto nivel no debe depender de implementaciones concretas de un componente de bajo nivel. En su lugar, ambos deben depender de abstracciones (interfaces).
En cristiano, A una clase no le pueden llegar FLECHAS en el diagrama de componentes. No puedo hacer imports de una clase a otra... al menos clases gordas de verdad...
No clases de mentirijilla (PalabraNoEncontrada, IdiomaNoSoportado estan bien)

Si quito el import , necesito quitar el new. Y vuelvo al problema: Necesito una implementación concreta de SuministradorDeDiccionarios, pero no quiero depender de ella directamente.

Hay muchos PATRONES de desarrollo que me ayudan a resolver este problema... Algunos son muy viejos, y ya no los usamos... Otros se han vuelto virales y son los que todo el mundo usa:
- Singleton
- Factoría
- Service Locator (De hecho este es el que regala java) Y el que voy a explicar... -> Modulos (aunque los módulos van mucho más allá de esto)
  Aunque llegó tarde y mal! Y la industría ya se había movido... a Spring. Y al uso de Inyección de Dependencias.
- Patrón de Inyección de Dependencias (Dependency Injection) <- En JAVA QUIEN ES EL EXPERTO EN ESTO? Spring Framework

---

# NOTA:

Empezamos a ver cierto patrón en el curso y en las funcionalidades de JAVA.
Paece que muchas cosas en JAVA llegaron tarde y mal.

Por qué? Qué ha pasado con JAVA?

Recordaís año 2000-2005? JAVA ERA EL LENGUAJE DE MODA. Todo el mundo quería aprender JAVA.
Toda app neva se creaba en JAVA.

- App de escritorio : JAVA
- App web : JAVA (Servlets, JSP)
- App móvil : JAVA (Android)
- App embebida : JAVA (Java ME)

Y si miramos la foto a fecha 2026? Todo el mundo quiere aprender JAVA? NO (python, js, ts)

Las apps de escritorio se hacen en java? NO. Hemos vuelto a lenguajes tradicionales o incluso estamos usando lenguajes nuevos.
Apps WEB?
 - Frontal - JS/TS
 - Backend - Java (Spring)
App Mobile Android? Kotlin
Apps embebidas: C, python

Qué ha pasado con JAVA? 

Historial de versiones de JAVA:

- JAVA 1.0 (1996)    1 versión al año
- JAVA 1.1 (1997) 
- JAVA 1.2 (1998) 
- JAVA 1.3 (2000)    2 versiones al año
- JAVA 1.4 (2002) 
- JAVA 1.5 (2004) 
- JAVA 1.6 (2006) 
- JAVA 1.7 (2011)    Qué ha pasado? 5 años para una versión?
- JAVA 1.8 (2014)    3 años para la siguiente?
- JAVA 9 (2017)      Otros 3 para la siguiente?
                        QUE HA PASADO? ORACLE! Que compró sun micro-systems. Y abandonó JAVA... A ORACLE le importaba NADA JAVA... Solo quería el HARDWARE de Sun.
- JAVA 10 (2018) 
- JAVA 11 (2018) 
- JAVA 12 (2019) 
- JAVA 13 (2019) 
- JAVA 14 (2020) 
- JAVA 15 (2020) 
- JAVA 16 (2021) 
- JAVA 17 (2021) 
- JAVA 18 (2022) 
- JAVA 19 (2022) 
- JAVA 20 (2023) 
- JAVA 21 (2023) 
- JAVA 22 (2024) 
- JAVA 23 (2024) 
- JAVA 24 (2025) 
- JAVA 25 (2025) 
- JAVA 26 (2026) 
- JAVA 27 (2026) 

---

# Proyecto Jigsaw: Modularización de la máquina virtual y del JDK

Hasta java 8, teníamos :
- Clases
- Interfaces
- Enumeraciones (Enums)
- Paquetes (Packages)

Y había una jerarquía:

- Paquetes
  - Clases
  - Interfaces
  - Enumeraciones (Enums)

En java 9, aparece un nuevo nivel de agrupación / tipo de organización llamado Módulo (Module).

- Modulo
  - Paquetes
    - Clases
    - Interfaces
    - Enumeraciones (Enums)

Y aparecen los ficheros module-info.java que definen los módulos y sus dependencias.

Ejemplo de un fichero module-info.java:

```java
module diccionarios-api {
    exports com.curso.diccionarios.api;
}


module implementacion-diccionarios-ficheros {
    requires diccionarios-api;  // Este módulo DEPENDE del módulo diccionarios-api
    //exports com.curso.diccionarios.ficheros;  // Este paquete se exporta... Y por ende, todas las clases/interfaces/enumeraciones públicas de ese paquete
                                              // pueden ser accesibles desde otros módulos que requieran este módulo.
                                              // El paquete com.curso.diccionarios.ficheros.utils no está exportado, por lo que sus clases/interfaces/enumeraciones públicas no serán accesibles desde otros módulos.
    provides com.curso.diccionarios.api.SuministradorDiccionarios with com.curso.diccionarios.ficheros.SuministradorDiccionariosFicheros;
    // Al proveer esa implementación de una interfaz, ni siquiera es necesario exportar el paquete donde se encuentra la implementación.
    // Y esto es guay. NADIE (salvo la JVM) podrá acceder a la implementación directamente.
}
// Pero no implica que luego pueda tener:
module servicio-diccionarios-api {
    exports com.curso.servicio.diccionarios.api;
}
module servicio-diccionarios-impl {
    requires servicio-diccionarios-api;
    uses com.curso.diccionarios.api.SuministradorDiccionarios;
}
```

Y esto permite que en el código de ServicioDiccionariosImpl pueda usar el service Loader.
Nadie salvo el ServiceLoader podrá acceder a las implementaciones de SuministradorDeDiccionarios.
```java
package com.curso.servicio.impl;

import com.curso.servicio.api.ServicioDeDiccionarios;
import com.curso.servicio.api.ResultadoBusqueda;
import com.curso.diccionarios.api.SuministradorDeDiccionarios;
import com.curso.diccionarios.api.Diccionario;
import com.curso.servicio.api.PalabraEncontrada;
import com.curso.servicio.api.PalabraNoEncontrada;
import com.curso.servicio.api.IdiomaNoSoportado;
import java.util.ServiceLoader;

public class ServicioDeDiccionariosImpl implements ServicioDeDiccionarios {
    @Override
    public ResultadoBusqueda buscarSignificados(String palabra, String idioma) throws Exception {
        SuministradorDeDiccionarios suministrador = ServiceLoader.load(SuministradorDeDiccionarios.class).findFirst().orElseThrow();
        if(suministrador.tienesDiccionarioDe(idioma)) {
            var diccionario = suministrador.getDiccionario(idioma).get(); // Esta excepción se debería producir en algún caso? Si hay un bug.
            if(diccionario.existe(palabra)) {
                return new PalabraEncontrada(idioma, palabra,diccionario.getSignificados(palabra).get());
            } else {
                return new PalabraNoEncontrada(idioma, palabra);
            }
        }else{
            return new IdiomaNoSoportado(idioma);
        }
    }
}
```

Los módulos no quedan solo aquí. Me permiten decidir que PAQUETES/MODULOS se cargan en la JVM cuando arranca.
El API DE JAVA es grande! Y ocupa mucho en RAM.
Con los módulos puedo cargar solo lo que necesito, reduciendo el consumo de memoria y mejorando el rendimiento.
ESTE ERA EL OBJETIVO INICIAL DEL PROYECTO: Modularizar la jvm.

De paso se añadieron más cositas:
- Control más eficiente y efectivo de qué clases y paquetes se exponen y se pueden usar desde otros módulos.
- ServiceLoader mejorado, que permite cargar implementaciones de interfaces de manera dinámica y modular.

Está guay... aunque como decía antes: LA INDUSTRIA YA HABIA MOVIDO FICHE PREVIAMENTE: SPRING!

Spring hoy en día es el gran framework de desarrollo en Java, que ha adoptado muchas de las ideas de modularidad y carga dinámica de componentes, pero con su propio ecosistema y convenciones.

No hay aplicación nueva que se desarrolle en JAVA a día de hoy que vaya sin Spring. Se ha convertido en el estandar de facto.

Hay cositas que usan internamente módulos. 
Por ejemplo si miramos el código de JUNIT 5, veremos que hace uso de módulos para organizar su código y cargar extensiones de manera dinámica.
Para librerías es muy útil. Para aplicaciones de backend lo usamos menos... por no decir casi nunca. Por que TODO SE MONTA HOY EN DÍA CON SPRING Y SU ECOSISTEMA.
