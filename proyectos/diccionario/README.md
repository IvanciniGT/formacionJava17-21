# Diccionario

Busca una palabra en el diccionario de un idioma y muestra sus significados con ejemplos.

```
$ buscarPalabra es MELON
La palabra melón existe en el idioma ES y tiene los siguientes significados:
- Fruto grande, redondo y de pulpa jugosa y dulce.
    Ej: Me gusta comer melón!
- Persona con pocas luces.
    Ej: Eres un melón
    Ej: No seas melón
```

Es el proyecto del curso **Java LTS: de Java 8 a Java 21**, y está contado **rama a rama**: empieza
siendo una app de consola con ficheros y acaba con un servidor central, una base de datos y un
frontal web, sin reescribir lo que ya funcionaba. Cada paso añade piezas nuevas y toca lo mínimo.
Este README es el mismo en todas las ramas: cuenta el recorrido entero.

`v1.0-consola-ficheros` → `v1.1-control-de-configuracion` → `v1.2-repositorio-que-avisa-de-errores` → `v2.0-servidor-central` → `v3.0-bbdd` → `v3.1-sugerencias` → `v4.0-frontal-web`

| Rama | Etapa |
|---|---|
| [`v1.0-consola-ficheros`](#v10--consola-con-ficheros--rama-v10-consola-ficheros) | Consola con ficheros |
| [`v1.1-control-de-configuracion`](#v11--control-de-configuración--rama-v11-control-de-configuracion) | Control de configuración |
| [`v1.2-repositorio-que-avisa-de-errores`](#v12--un-repositorio-que-avisa-de-los-errores--rama-v12-repositorio-que-avisa-de-errores) | Un repositorio que avisa de los errores |
| [`v2.0-servidor-central`](#v20--servidor-central--rama-v20-servidor-central) | Servidor central |
| [`v3.0-bbdd`](#v30--diccionarios-en-bbdd--rama-v30-bbdd) | Diccionarios en BBDD |
| [`v3.1-sugerencias`](#v31--palabras-sugeridas--rama-v31-sugerencias) | Palabras sugeridas |
| [`v4.0-frontal-web`](#v40--frontal-web--rama-v40-frontal-web) | Frontal web |

En qué rama estás: `git branch --show-current`. Todas compilan, pasan sus pruebas y funcionan.

## Las piezas

Un sistema de componentes desacoplados, cada uno con su ciclo de vida:

| Componente | Qué es |
|---|---|
| `diccionarios-api` | El REPOSITORIO (lógica de persistencia): `SuministradorDeDiccionarios`, `Diccionario`, `Significado`. Funciones de bajo nivel, casi un SELECT. Con sus pruebas de contrato (`test-jar`) |
| `diccionarios-impl-ficheros` | El repositorio en ficheros: lee los `.txt` de los jars de idioma |
| `diccionario-es` | Solo datos: el diccionario de español y su índice |
| `diccionario-en` | Solo datos: el diccionario de inglés y su índice |
| `diccionarios-service-api` | El SERVICIO (lógica de negocio): `BuscadorDePalabras` y su resultado sellado `ResultadoDeBusqueda` (`PalabraEncontrada`, `PalabraNoEncontrada`, `IdiomaNoSoportado`) |
| `diccionarios-service-impl` | El servicio de verdad, sobre el repositorio. Con las pruebas de contrato del servicio (`test-jar`) |
| `ui-api` | La interfaz de usuario: todo lo que la app le dice o le pide al usuario |
| `ui-impl-consola` | La de línea de comandos: lee idioma y palabra de los argumentos |
| `diccionarios-app-consola` | El código de la app de consola: el `main`, las **factorías** y `ProcesadorDePeticiones`, que expone el servicio por la línea de comandos |
| `diccionarios-app-consola-ficheros` | LA APLICACIÓN, el ensamblado: un pom sin código que dice qué piezas lleva. Todo en local, con ficheros |
| `diccionarios-controlador-rest-api` | El contrato HTTP, independiente: rutas, DTO, errores, y la especificación OpenAPI que se genera de él |
| `diccionarios-controlador-rest` | El `@RestController`: expone el servicio por HTTP |
| `diccionarios-service-impl-rest` | Otro servicio... que se lo pregunta al servidor por HTTP (`java.net.http`): traduce llamadas al API Java en peticiones REST |
| `diccionarios-app-consola-servidor` | Otra aplicación, otro ensamblado del mismo código: la consola contra el servidor |
| `diccionarios-app-servidor` | La aplicación del servidor (Spring Boot): las factorías convertidas en `@Bean` |
| `diccionarios-impl-bbdd` | El repositorio en BBDD (Spring Data JPA); se enchufa solo por autoconfiguración |
| `cargador-de-bbdd-palabras` | Llena la BBDD con los jars de idioma al arrancar el servidor, si han cambiado (huella SHA-256) |
| `diccionarios-web` | El frontal web, Angular 21: otro cliente más del mismo servidor |

### Versión de cada componente en cada etapa

Control de configuración: cada componente versiona por su cuenta, y cada aplicación dice qué
versión de cada pieza lleva. En negrita, lo que cambia en esa etapa.

| Componente | v1.0 | v1.1 | v1.2 | v2.0 | v3.0 | v3.1 | v4.0 |
|---|---|---|---|---|---|---|---|
| `diccionarios-api` | 1.0.0 | 1.0.0 | **1.1.0** | 1.1.0 | 1.1.0 | 1.1.0 | 1.1.0 |
| `diccionarios-impl-ficheros` | 1.0.0 | 1.0.0 | **1.1.0** | 1.1.0 | 1.1.0 | 1.1.0 | 1.1.0 |
| `diccionario-es` | 1.0.0 | **1.1.0** | 1.1.0 | 1.1.0 | 1.1.0 | 1.1.0 | 1.1.0 |
| `diccionario-en` | 1.0.0 | **1.0.1** | 1.0.1 | 1.0.1 | 1.0.1 | 1.0.1 | 1.0.1 |
| `diccionarios-service-api` | 1.0.0 | 1.0.0 | 1.0.0 | 1.0.0 | 1.0.0 | **1.1.0** | 1.1.0 |
| `diccionarios-service-impl` | 1.0.0 | 1.0.0 | **1.1.0** | 1.1.0 | 1.1.0 | **1.2.0** | 1.2.0 |
| `ui-api` | 1.0.0 | 1.0.0 | 1.0.0 | 1.0.0 | 1.0.0 | **1.1.0** | 1.1.0 |
| `ui-impl-consola` | 1.0.0 | **1.0.1** | 1.0.1 | 1.0.1 | 1.0.1 | **1.1.0** | 1.1.0 |
| `diccionarios-app-consola` | 1.0.0 | 1.0.0 | 1.0.0 | 1.0.0 | 1.0.0 | **1.1.0** | 1.1.0 |
| `diccionarios-app-consola-ficheros` | 1.0.0 | **1.1.0** | **1.2.0** | 1.2.0 | 1.2.0 | **1.3.0** | 1.3.0 |
| `diccionarios-controlador-rest-api` |  |  |  | **1.0.0** | 1.0.0 | **1.1.0** | 1.1.0 |
| `diccionarios-controlador-rest` |  |  |  | **1.0.0** | 1.0.0 | **1.1.0** | 1.1.0 |
| `diccionarios-service-impl-rest` |  |  |  | **1.0.0** | 1.0.0 | **1.1.0** | 1.1.0 |
| `diccionarios-app-consola-servidor` |  |  |  | **1.0.0** | 1.0.0 | **1.1.0** | 1.1.0 |
| `diccionarios-app-servidor` |  |  |  | **1.0.0** | **2.0.0** | **2.1.0** | 2.1.0 |
| `diccionarios-impl-bbdd` |  |  |  |  | **1.0.0** | 1.0.0 | 1.0.0 |
| `cargador-de-bbdd-palabras` |  |  |  |  | **1.0.0** | 1.0.0 | 1.0.0 |
| `diccionarios-web` |  |  |  |  |  |  | **1.0.0** |

## Requisitos

- JDK 21 o superior (se compila con `release 21`; probado con JDK 25).
- Maven no hace falta: va el **Maven wrapper**. `./mvnw` (en Windows, `mvnw.cmd`) se baja la versión
  que toca la primera vez.
- Para el frontal web (`v4.0`): Node.js 20+ y npm.

## Comandos que valen en todas las etapas

Desde `proyectos/diccionario`:

```bash
./mvnw install                 # compila, pasa todas las pruebas y deja los jars en target/ y en ~/.m2
./mvnw verify                  # lo mismo, más el informe de cobertura de cada módulo con pruebas
                               # (target/site/jacoco/index.html)
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="es MELON"
```

- `exec:exec` necesita que antes se haya hecho `./mvnw install`.
- **Sin argumentos, la app sale con código 1** (error de uso; con 2, un error interno). Es a
  propósito, para que un script sepa qué pasó, y Maven lo enseña como un fallo del proceso
  (`ExecuteException ... Exit value: 1`) después de la ayuda. No es un error del proyecto.
- `--add-modules ALL-MODULE-PATH`, en los comandos `java`: las implementaciones y los diccionarios
  no los requiere ningún módulo (se encuentran por el `ServiceLoader` y por el class loader), y un
  módulo que nadie requiere no se carga. En Windows, el separador del *module path* es `;`.
- Con `DICCIONARIOS_CARPETA=/ruta/` se leen los `<idioma>.txt` de una carpeta del disco en vez de
  los jars.
- La especificación OpenAPI la genera `diccionarios-controlador-rest-api` al empaquetar (desde la
  `v2.0`): queda en su `target/openapi.yaml` y **dentro de su jar**, en `openapi/diccionarios.yaml`.
  Con el servidor arrancado, también en `http://localhost:8080/v3/api-docs` y en Swagger UI
  (`http://localhost:8080/swagger-ui.html`).

> `./mvnw clean` borra el `target/` de un proceso que esté corriendo (el servidor, por ejemplo): la
> JVM sigue viva pero no puede cargar clases nuevas y deja de responder sin decir nada. Reinícialo
> después de cada `clean`.

## Etapa a etapa

### v1.0 · Consola con ficheros — rama `v1.0-consola-ficheros`

La primera versión que funciona: una app de consola con los diccionarios en ficheros. Y ya en
componentes desacoplados, para que cuando lleguen cambios (y llegarán) no haya que **modificar ni
una línea de código**: crear piezas nuevas, quitar las que sobren, pero no tocar lo que funciona.

- **El repositorio** (`diccionarios-api` + `diccionarios-impl-ficheros`): LÓGICA DE PERSISTENCIA.
  Funciones de muy bajo nivel: `getDiccionario(idioma)`, `getSignificados(palabra)`, `getPalabras()`.
  `Optional` en lugar de `null`. Con sus **pruebas de contrato**: lo que *cualquier* implementación
  tiene que cumplir.
- **El servicio de búsqueda** (`diccionarios-service-api` + `-impl`): LÓGICA DE NEGOCIO, no un
  agregado del repositorio. `buscarExacta` y `buscarIgnorandoMayusculasYAcentos` (*MEMBRILLO* →
  *membrillo*, *Atun* → *atún*; pero *ano* no es *año*). Devuelve tres resultados distintos, y no un
  `Optional` (que no diría si falta la palabra o el idioma): un **`sealed interface
  ResultadoDeBusqueda permits PalabraEncontrada, PalabraNoEncontrada, IdiomaNoSoportado`**, tres
  `record`. Un contrato cerrado (Liskov).
- **La interfaz de usuario, aparte** (`ui-api` / `ui-impl-consola`).
- **Los diccionarios, cada uno su módulo** (`diccionario-es`, `diccionario-en`): independientes entre
  sí y del resto. Los mantienen lingüistas, no desarrolladores.
- **La aplicación**, el ensamblado: `diccionarios-app-consola` es el código (el `main`, las
  **factorías** y el procesador, que trata el resultado con un **`switch` con patrones de Java 21**,
  exhaustivo y sin `default`), y `diccionarios-app-consola-ficheros` es lo que se entrega: un pom sin
  código que dice qué piezas, y en qué versión, lleva.
- Las factorías no hacen el `new` de nada: se lo piden al **`ServiceLoader`** (`uses` / `provides` en
  `module-info.java`), que da lo que haya en el module path.

Y funciona. Pero se instala en cada máquina: cada errata en un diccionario, cada cambio, obliga a
que todos los usuarios actualicen.

**Componentes que nacen en esta etapa:** `diccionarios-api` 1.0.0, `diccionarios-impl-ficheros` 1.0.0, `diccionario-es` 1.0.0, `diccionario-en` 1.0.0, `diccionarios-service-api` 1.0.0, `diccionarios-service-impl` 1.0.0, `ui-api` 1.0.0, `ui-impl-consola` 1.0.0, `diccionarios-app-consola` 1.0.0, `diccionarios-app-consola-ficheros` 1.0.0.

#### Pruébalo

```bash
git switch v1.0-consola-ficheros
cd proyectos/diccionario
./mvnw install          # con todas sus pruebas
```

```bash
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="es MELON"
```
```
La palabra melón existe en el idioma ES y tiene los siguientes significados:
- Fruto grande, redondo y de pulpa jugosa y dulce.
    Ej: Me gusta comer melón!
- Persona con pocas luces.
    Ej: Eres un melón
    Ej: No seas melón
```

```bash
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="es archilococo"
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="elf casa"
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="en melon"
```
```
La palabra archilococo NO existe en el idioma es.
Lo siento, pero no tengo diccionario para el idioma elf.
La palabra melon existe en el idioma EN y tiene los siguientes significados:
- A large round fruit with swet juicy flesh.
    Ej: A slyce of melon
```

`swet` y `slyce`: erratas del diccionario de inglés 1.0.0, que corrige la `v1.1`. Y `MEMBRILLO`
todavía no existe: llega con el diccionario de español 1.1.0.

Con `java` directamente, la consola de ficheros (las piezas que dice su pom):

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.0.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.0.0.jar:diccionarios-service-impl/target/diccionarios-service-impl-1.0.0.jar:ui-api/target/ui-api-1.0.0.jar:ui-impl-consola/target/ui-impl-consola-1.0.0.jar:diccionarios-impl-ficheros/target/diccionarios-impl-ficheros-1.0.0.jar:diccionario-es/target/diccionario-es-1.0.0.jar:diccionario-en/target/diccionario-en-1.0.0.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.0.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es MELON
```

### v1.1 · Control de configuración — rama `v1.1-control-de-configuracion`

**Control de configuración.** Llegan cambios, y cada uno se queda en su componente:

- Nuevas palabras en español: `diccionario-es` **1.1.0** (*membrillo*, *atún*, *sandía*...).
- Erratas en las definiciones de inglés: `diccionario-en` **1.0.1**.
- Otros mensajes de ayuda en la consola: `ui-impl-consola` **1.0.1**.

Y una versión nueva de la aplicación, `diccionarios-app-consola-ficheros` **1.1.0**, que solo dice
que ahora lleva esas versiones. El repositorio, el servicio y el código de la app no se tocan.

```bash
git diff v1.0-consola-ficheros v1.1-control-de-configuracion --stat
```

Datos, un texto de ayuda y poms: ni una línea de lógica.

**Componentes que cambian en esta etapa:** `diccionario-es` 1.1.0, `diccionario-en` 1.0.1, `ui-impl-consola` 1.0.1, `diccionarios-app-consola-ficheros` 1.1.0.

```bash
git diff v1.0-consola-ficheros v1.1-control-de-configuracion --stat     # qué ha cambiado desde la anterior
```

#### Pruébalo

```bash
git switch v1.1-control-de-configuracion
cd proyectos/diccionario
./mvnw install          # con todas sus pruebas
```

```bash
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="es MEMBRILLO"
```
```
La palabra membrillo existe en el idioma ES y tiene los siguientes significados:
- Fruto del membrillero, amarillo y muy aromático.
    Ej: El membrillo crudo es muy áspero
- Dulce que se hace con ese fruto.
    Ej: Pan con queso y membrillo
```

```bash
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="es Atun"
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="en melon"
```
```
La palabra atún existe en el idioma ES y tiene los siguientes significados:
- Pez marino de gran tamaño, de carne muy apreciada.
    Ej: Una lata de atún
- Persona torpe o ignorante.
    Ej: No seas atún
La palabra melon existe en el idioma EN y tiene los siguientes significados:
- A large round fruit with sweet juicy flesh.
    Ej: A slice of melon
```

Sin argumentos, la ayuda nueva de `ui-impl-consola` 1.0.1:

```bash
./mvnw exec:exec -pl diccionarios-app-consola-ficheros
```
```
Faltan argumentos.
Uso:

    $ buscarPalabra <IDIOMA> <PALABRA>

El idioma es su código (ES, EN...), en mayúsculas o en minúsculas. La palabra
se encuentra aunque no lleve sus acentos ni sus mayúsculas: "Atun" encuentra "atún".

Ejemplos:

    $ buscarPalabra es melón
    $ buscarPalabra ES MEMBRILLO
```

Con `java` directamente, la consola de ficheros (las piezas que dice su pom):

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.0.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.0.0.jar:diccionarios-service-impl/target/diccionarios-service-impl-1.0.0.jar:ui-api/target/ui-api-1.0.0.jar:ui-impl-consola/target/ui-impl-consola-1.0.1.jar:diccionarios-impl-ficheros/target/diccionarios-impl-ficheros-1.0.0.jar:diccionario-es/target/diccionario-es-1.1.0.jar:diccionario-en/target/diccionario-en-1.0.1.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.0.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es MELON
```

### v1.2 · Un repositorio que avisa de los errores — rama `v1.2-repositorio-que-avisa-de-errores`

Antes de llevar nada a un servidor hay que arreglar una cosa del repositorio: en un servidor (con
BBDD, con red) **las cosas fallan**, y la 1.0.0 no tiene por dónde decirlo. Un fichero ilegible, o
una BBDD caída, acaban pareciendo *"no existe"*.

- **Repositorio 1.1.0**: cada método tiene un hermano que sí avisa (`throws Exception`):
  `existeLaPalabra`, `buscarSignificados`, `listarPalabras`, `tienesDiccionarioDeIdioma`,
  `getDiccionarioBuena`, `getIdiomasDisponibles`. Con **`default`** que delega en el viejo, y el viejo
  **`@Deprecated(forRemoval = true)`**. Las implementaciones existentes siguen compilando sin tocarlas:
  es un **minor**. Para eso existen los `default`, no para meter lógica compartida.
- El repositorio en ficheros 1.1.0 los implementa, y su cache pasa a `ConcurrentHashMap`: un servidor
  atiende peticiones en paralelo, y el `WeakHashMap` no es seguro entre hilos.
- **El servicio absorbe el cambio** (1.1.0): convierte los errores en `BuscadorDePalabrasException`.
  Su API no cambia...
- ... y por eso el código de la app de consola **no cambia ni una línea**.

```bash
git diff v1.1-control-de-configuracion v1.2-repositorio-que-avisa-de-errores -- proyectos/diccionario/diccionarios-api
```

**Componentes que cambian en esta etapa:** `diccionarios-api` 1.1.0, `diccionarios-impl-ficheros` 1.1.0, `diccionarios-service-impl` 1.1.0, `diccionarios-app-consola-ficheros` 1.2.0.

```bash
git diff v1.1-control-de-configuracion v1.2-repositorio-que-avisa-de-errores --stat     # qué ha cambiado desde la anterior
```

#### Pruébalo

```bash
git switch v1.2-repositorio-que-avisa-de-errores
cd proyectos/diccionario
./mvnw install          # con todas sus pruebas
```

Por fuera, lo mismo que la `v1.1` (y es la gracia: el repositorio ha cambiado y la consola no se
entera):

```bash
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="es Atun"
```
```
La palabra atún existe en el idioma ES y tiene los siguientes significados:
...
```

Lo nuevo se ve en las pruebas (`./mvnw install`): el contrato del repositorio comprueba que los
métodos nuevos dicen lo mismo que los viejos, y el del servicio, que un fallo del repositorio llega
como `BuscadorDePalabrasException`.

Con `java` directamente, la consola de ficheros (las piezas que dice su pom):

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.0.0.jar:diccionarios-service-impl/target/diccionarios-service-impl-1.1.0.jar:ui-api/target/ui-api-1.0.0.jar:ui-impl-consola/target/ui-impl-consola-1.0.1.jar:diccionarios-impl-ficheros/target/diccionarios-impl-ficheros-1.1.0.jar:diccionario-es/target/diccionario-es-1.1.0.jar:diccionario-en/target/diccionario-en-1.0.1.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.0.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es MELON
```

### v2.0 · Servidor central — rama `v2.0-servidor-central`

**Arquitectura distribuida, cliente-servidor.** Para qué: que los cambios en las palabras, y en
**cómo** se usan los diccionarios (el servicio), no obliguen a actualizar todas las máquinas.

**No hay que cambiar nada del código.** Solo se crean componentes nuevos:

- `diccionarios-controlador-rest-api`: el **contrato HTTP**, independiente, del que springdoc genera
  la especificación **OpenAPI**. Se escribe una sola vez (DRY).
- `diccionarios-controlador-rest`: expone por REST las funciones del servicio, en el backend.
- `diccionarios-service-impl-rest`: otra implementación del mismo `BuscadorDePalabras`, que traduce
  cada llamada al API Java en una petición HTTP (`java.net.http.HttpClient`, Java 11).
- `diccionarios-app-servidor`: el servidor (Spring Boot). Las **factorías** de la consola se
  convierten en **`@Bean`**: aquí el `new` lo hace Spring (inversión de control), y estos métodos le
  dicen de dónde sale cada pieza (inyección de dependencias).
- `diccionarios-app-consola-servidor`: otra aplicación, otro ensamblado **del mismo código** de
  consola: con el servicio remoto en vez del local.

Y en paralelo sigue estando `diccionarios-app-consola-ficheros`, 100 % local. Las probabilidades de
estropear algo son bajas: no se ha tocado nada de lo que ya funcionaba, y lo nuevo se prueba a
fondo. El servicio remoto pasa **las mismas pruebas de contrato** que el local, contra el servidor de
verdad.

```bash
git diff v1.2-repositorio-que-avisa-de-errores v2.0-servidor-central --stat   # solo ficheros nuevos, y el pom raíz
```

**Componentes que cambian en esta etapa:** `diccionarios-controlador-rest-api` **nuevo** 1.0.0, `diccionarios-controlador-rest` **nuevo** 1.0.0, `diccionarios-service-impl-rest` **nuevo** 1.0.0, `diccionarios-app-consola-servidor` **nuevo** 1.0.0, `diccionarios-app-servidor` **nuevo** 1.0.0.

```bash
git diff v1.2-repositorio-que-avisa-de-errores v2.0-servidor-central --stat     # qué ha cambiado desde la anterior
```

#### Pruébalo

```bash
git switch v2.0-servidor-central
cd proyectos/diccionario
./mvnw install          # con todas sus pruebas
```

El servidor, desde esta carpeta:

```bash
java -jar diccionarios-app-servidor/target/diccionarios-app-servidor-1.0.0.jar
```

```bash
curl localhost:8080/api/v1/diccionarios                                       # ["EN","ES"]
curl localhost:8080/api/v1/diccionarios/ES                                    # {"idioma":"ES"}
curl localhost:8080/api/v1/diccionarios/XX                                    # 404 {"codigo":"IDIOMA_NO_DISPONIBLE",...}
curl localhost:8080/api/v1/diccionarios/ES/palabras/MELON                     # 404 {"codigo":"PALABRA_NO_ENCONTRADA",...}
curl "localhost:8080/api/v1/diccionarios/ES/palabras/MELON?ignorarMayusculasYAcentos=true"
                                                                              # {"idioma":"ES","palabra":"melón","significados":[...]}
```

Y con el servidor arrancado, en otra terminal, la consola que se lo pregunta:

```bash
./mvnw exec:exec -pl diccionarios-app-consola-servidor -Dargumentos="es Atun"
```
```
La palabra atún existe en el idioma ES y tiene los siguientes significados:
- Pez marino de gran tamaño, de carne muy apreciada.
    Ej: Una lata de atún
- Persona torpe o ignorante.
    Ej: No seas atún
```

La misma salida que la de ficheros, que sigue funcionando igual, en paralelo.

Con `java` directamente, la consola de ficheros (las piezas que dice su pom):

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.0.0.jar:diccionarios-service-impl/target/diccionarios-service-impl-1.1.0.jar:ui-api/target/ui-api-1.0.0.jar:ui-impl-consola/target/ui-impl-consola-1.0.1.jar:diccionarios-impl-ficheros/target/diccionarios-impl-ficheros-1.1.0.jar:diccionario-es/target/diccionario-es-1.1.0.jar:diccionario-en/target/diccionario-en-1.0.1.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.0.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es MELON
```
Y la consola contra el servidor:

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.0.0.jar:ui-api/target/ui-api-1.0.0.jar:ui-impl-consola/target/ui-impl-consola-1.0.1.jar:diccionarios-service-impl-rest/target/diccionarios-service-impl-rest-1.0.0.jar:diccionarios-controlador-rest-api/target/diccionarios-controlador-rest-api-1.0.0.jar:$HOME/.m2/repository/com/google/code/gson/gson/2.14.0/gson-2.14.0.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.0.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es Atun
```

### v3.0 · Diccionarios en BBDD — rama `v3.0-bbdd`

Con todo ya en el servidor, los diccionarios pasan a una **BBDD** (H2, con Spring Data JPA).

- ¿Hay que cambiar el repositorio en ficheros? Por dentro, no: hay que **sacarlo del ensamblado** y
  meter uno nuevo, `diccionarios-impl-bbdd`, que se enchufa solo por autoconfiguración de Spring.
- ¿Y de dónde saca los datos la BBDD? De un componente nuevo, **`cargador-de-bbdd-palabras`**: al
  arrancar el servidor compara cada fichero de idioma con lo que hay cargado y, si ha cambiado, borra
  ese idioma y lo vuelve a cargar, **en una transacción**. Se compara la **huella** (SHA-256) del
  fichero, y no el número de palabras: arreglar una errata (`diccionario-en` 1.0.1) no cambia cuántas
  hay, y también tiene que llegar a la BBDD.
- `diccionarios-app-servidor` **2.0.0**: un **major**. No por el código, que no cambia, sino porque
  ahora necesita una BBDD: no funciona en el mismo entorno que la 1.0.0.
- Ni el servicio, ni el controlador, ni las dos apps de consola se enteran.

**Componentes que cambian en esta etapa:** `diccionarios-app-servidor` 2.0.0, `diccionarios-impl-bbdd` **nuevo** 1.0.0, `cargador-de-bbdd-palabras` **nuevo** 1.0.0.

```bash
git diff v2.0-servidor-central v3.0-bbdd --stat     # qué ha cambiado desde la anterior
```

#### Pruébalo

```bash
git switch v3.0-bbdd
cd proyectos/diccionario
./mvnw install          # con todas sus pruebas
```

```bash
java -jar diccionarios-app-servidor/target/diccionarios-app-servidor-2.0.0.jar
```

En el log, al primer arranque (la BBDD se crea en `./datos/`):

```
Diccionario EN cargado en BBDD (huella ...)
Diccionario ES cargado en BBDD (huella ...)
```

Durante esos segundos el servidor ya responde, pero sin idiomas: espera a que
`curl localhost:8080/api/v1/diccionarios` dé `["EN","ES"]`. Al segundo arranque, sin haber cambiado
ningún jar de idioma:

```
Diccionario EN sin cambios (huella ...): no se recarga
Diccionario ES sin cambios (huella ...): no se recarga
```

Las dos consolas, igual que en la `v2.0`.

Con `java` directamente, la consola de ficheros (las piezas que dice su pom):

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.0.0.jar:diccionarios-service-impl/target/diccionarios-service-impl-1.1.0.jar:ui-api/target/ui-api-1.0.0.jar:ui-impl-consola/target/ui-impl-consola-1.0.1.jar:diccionarios-impl-ficheros/target/diccionarios-impl-ficheros-1.1.0.jar:diccionario-es/target/diccionario-es-1.1.0.jar:diccionario-en/target/diccionario-en-1.0.1.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.0.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es MELON
```
Y la consola contra el servidor:

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.0.0.jar:ui-api/target/ui-api-1.0.0.jar:ui-impl-consola/target/ui-impl-consola-1.0.1.jar:diccionarios-service-impl-rest/target/diccionarios-service-impl-rest-1.0.0.jar:diccionarios-controlador-rest-api/target/diccionarios-controlador-rest-api-1.0.0.jar:$HOME/.m2/repository/com/google/code/gson/gson/2.14.0/gson-2.14.0.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.0.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es Atun
```

### v3.1 · Palabras sugeridas — rama `v3.1-sugerencias`

Un cambio **funcional**: si la palabra no existe, el sistema sugiere las parecidas.

```
$ buscarPalabra es membrilo
La palabra membrilo NO existe en el idioma es.
Quizás quisiste decir: membrillo
```

Toca un montón de componentes, pero está muy claro qué cambia en cada uno:

- **API del servicio 1.1.0**: ningún método nuevo; solo crece una clase de transporte de datos,
  `PalabraNoEncontrada`, que gana sus sugerencias. Con cuidado de no romper a nadie: su constructor
  de antes sigue existiendo, así que es un **minor**.
- **Las dos implementaciones del servicio**: la local (1.2.0) calcula las parecidas con
  **Levenshtein**, que es lógica de negocio (`PalabrasSimilares`, con un `record` local y
  `parallelStream`); la remota (1.1.0) las lee de la respuesta.
- **El controlador y su API** (1.1.0): las sugerencias viajan **en el cuerpo del 404** de palabra no
  encontrada. Un campo nuevo en una respuesta no rompe a quien ya la leía.
- **La interfaz de usuario** (`ui-api` y `ui-impl-consola` 1.1.0) las enseña.

El repositorio no cambia: le basta con la lista de palabras que ya daba.

**Componentes que cambian en esta etapa:** `diccionarios-service-api` 1.1.0, `diccionarios-service-impl` 1.2.0, `ui-api` 1.1.0, `ui-impl-consola` 1.1.0, `diccionarios-app-consola` 1.1.0, `diccionarios-app-consola-ficheros` 1.3.0, `diccionarios-controlador-rest-api` 1.1.0, `diccionarios-controlador-rest` 1.1.0, `diccionarios-service-impl-rest` 1.1.0, `diccionarios-app-consola-servidor` 1.1.0, `diccionarios-app-servidor` 2.1.0.

```bash
git diff v3.0-bbdd v3.1-sugerencias --stat     # qué ha cambiado desde la anterior
```

#### Pruébalo

```bash
git switch v3.1-sugerencias
cd proyectos/diccionario
./mvnw install          # con todas sus pruebas
```

```bash
./mvnw exec:exec -pl diccionarios-app-consola-ficheros -Dargumentos="es membrilo"
```
```
La palabra membrilo NO existe en el idioma es.
Quizás quisiste decir: membrillo
```

Con el servidor (`java -jar diccionarios-app-servidor/target/diccionarios-app-servidor-2.1.0.jar`):

```bash
curl localhost:8080/api/v1/diccionarios/ES/palabras/membrilo
# 404 {"codigo":"PALABRA_NO_ENCONTRADA","mensaje":"La palabra membrilo no existe en el idioma ES","sugerencias":["membrillo"]}
curl localhost:8080/api/v1/diccionarios/XX/palabras/casa
# 404 {"codigo":"IDIOMA_NO_DISPONIBLE","mensaje":"No hay diccionario para el idioma XX"}     (sin sugerencias)

./mvnw exec:exec -pl diccionarios-app-consola-servidor -Dargumentos="es membrilo"
```
```
La palabra membrilo NO existe en el idioma es.
Quizás quisiste decir: membrillo
```

Con `java` directamente, la consola de ficheros (las piezas que dice su pom):

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.1.0.jar:diccionarios-service-impl/target/diccionarios-service-impl-1.2.0.jar:ui-api/target/ui-api-1.1.0.jar:ui-impl-consola/target/ui-impl-consola-1.1.0.jar:diccionarios-impl-ficheros/target/diccionarios-impl-ficheros-1.1.0.jar:diccionario-es/target/diccionario-es-1.1.0.jar:diccionario-en/target/diccionario-en-1.0.1.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.1.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es MELON
```
Y la consola contra el servidor:

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.1.0.jar:ui-api/target/ui-api-1.1.0.jar:ui-impl-consola/target/ui-impl-consola-1.1.0.jar:diccionarios-service-impl-rest/target/diccionarios-service-impl-rest-1.1.0.jar:diccionarios-controlador-rest-api/target/diccionarios-controlador-rest-api-1.1.0.jar:$HOME/.m2/repository/com/google/code/gson/gson/2.14.0/gson-2.14.0.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.1.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es Atun
```

### v4.0 · Frontal web — rama `v4.0-frontal-web`

Un frontal **web**, en Angular, en paralelo con las apps de consola (no se quitan: no son
incompatibles). Contra el mismo servidor, que no cambia ni una línea.

- **`diccionarios-web`**, Angular 21, con la misma arquitectura en otro lenguaje: un puerto, un
  adaptador REST, uniones discriminadas con `switch` exhaustivo como el `sealed` de Java.
- **Sus tipos salen del contrato Java**: `npm run contrato` genera los DTO desde la OpenAPI de
  `diccionarios-controlador-rest-api`. El DRY de la `v2.0`, llegando hasta el frontal.
- Busca ignorando mayúsculas y acentos, y enseña las sugerencias que llegan en el 404, con enlace.
- Su `ng build` deja HTML, JS y CSS que se pueden servir desde cualquier servidor web (también
  desde el propio `diccionarios-app-servidor`, como contenido estático).

Detalle en [diccionarios-web/README.md](diccionarios-web/README.md).

**Componentes que cambian en esta etapa:** `diccionarios-web` **nuevo** 1.0.0.

```bash
git diff v3.1-sugerencias v4.0-frontal-web --stat     # qué ha cambiado desde la anterior
```

#### Pruébalo

```bash
git switch v4.0-frontal-web
cd proyectos/diccionario
./mvnw install          # con todas sus pruebas
```

Con el servidor arrancado (`java -jar diccionarios-app-servidor/target/diccionarios-app-servidor-2.1.0.jar`):

```bash
cd diccionarios-web
npm install
npm test           # 11 pruebas
npm start          # http://localhost:4200
```

`npm start` reenvía `/api` al servidor (`proxy.conf.json`). En `http://localhost:4200/es/membrilo` se
ve la palabra no encontrada y `membrillo` como enlace.

```bash
npm run build      # deja el frontal en dist/diccionarios-web/browser/
npm run contrato   # regenera los DTO desde diccionarios-controlador-rest-api/target/openapi.yaml
```

Con `java` directamente, la consola de ficheros (las piezas que dice su pom):

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.1.0.jar:diccionarios-service-impl/target/diccionarios-service-impl-1.2.0.jar:ui-api/target/ui-api-1.1.0.jar:ui-impl-consola/target/ui-impl-consola-1.1.0.jar:diccionarios-impl-ficheros/target/diccionarios-impl-ficheros-1.1.0.jar:diccionario-es/target/diccionario-es-1.1.0.jar:diccionario-en/target/diccionario-en-1.0.1.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.1.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es MELON
```
Y la consola contra el servidor:

```bash
java --module-path diccionarios-api/target/diccionarios-api-1.1.0.jar:diccionarios-service-api/target/diccionarios-service-api-1.1.0.jar:ui-api/target/ui-api-1.1.0.jar:ui-impl-consola/target/ui-impl-consola-1.1.0.jar:diccionarios-service-impl-rest/target/diccionarios-service-impl-rest-1.1.0.jar:diccionarios-controlador-rest-api/target/diccionarios-controlador-rest-api-1.1.0.jar:$HOME/.m2/repository/com/google/code/gson/gson/2.14.0/gson-2.14.0.jar:diccionarios-app-consola/target/diccionarios-app-consola-1.1.0.jar \
     --add-modules ALL-MODULE-PATH \
     --module app/com.curso.diccionario.app.consola.BuscarPalabra es Atun
```

## Los diccionarios

Un jar por idioma (`diccionario-es`, `diccionario-en`), con su fichero y su índice en
`META-INF/diccionarios/`. El formato es un contrato, aunque no sea Java: está en
[diccionarios-impl-ficheros/FORMATO-DE-DICCIONARIO.md](diccionarios-impl-ficheros/FORMATO-DE-DICCIONARIO.md).

Cada uno tiene unas pocas palabras reales escritas a mano y **5000 inventadas**, las mismas en los
dos idiomas: no hay de dónde sacar 5000 palabras reales con definición y traducción con licencia
para copiarlas. Las genera, siempre iguales, `herramientas/GeneradorDeDiccionarios.java`.

## Cómo evolucionan los API

| Versión | Etapa | Qué cambia | Por qué no rompe a nadie |
|---|---|---|---|
| repositorio 1.1.0 | `v1.2` | Métodos nuevos con `throws Exception` y `default` que delega en el viejo; los viejos, `@Deprecated(forRemoval = true)` | Las implementaciones existentes siguen compilando sin tocarlas: minor |
| servicio 1.1.0 | `v3.1` | `PalabraNoEncontrada` gana `sugerencias`; ningún método nuevo | Su constructor de antes sigue existiendo, y un `switch` con patrón de tipo sigue compilando. Lo único que no aguantaría es un *record pattern* con dos campos |
| contrato REST 1.1.0 | `v3.1` | El cuerpo del 404 de palabra gana `sugerencias` | Un campo nuevo en una respuesta no rompe a quien ya la leía |

Los deprecados se quedan a propósito: quitarlos rompería a quien los use y sería un **major**. Y hay
un major que no viene de ningún API: `diccionarios-app-servidor` 2.0.0 (`v3.0`), porque necesita una
BBDD que la 1.0.0 no necesitaba; no funciona en el mismo entorno.

## Pruebas

| Dónde | Qué prueban | Desde |
|---|---|---|
| `diccionarios-api` | El **contrato del repositorio**: lo que cualquier implementación tiene que cumplir (`test-jar`). Lo cumple una implementación de referencia en memoria | `v1.0` |
| `diccionarios-impl-ficheros` | El contrato del repositorio, con ficheros generados; el formato; la lectura del classpath y del índice | `v1.0` |
| `diccionarios-service-impl` | El **contrato del servicio** (`test-jar`): las dos búsquedas, la ñ, los tres resultados, las reglas de normalización; desde la `v1.2`, los errores del repositorio; desde la `v3.1`, las sugerencias | `v1.0` |
| `ui-impl-consola`, `diccionarios-app-consola` | Los mensajes de la consola; el procesador con un servicio y una interfaz de usuario falsos, sin capturar la consola | `v1.0` |
| `diccionarios-service-impl-rest` | Contra un servidor falso (`com.sun.net.httpserver`, del JDK): cada 404, 500, un 404 que no es nuestro, URL mal codificada, servidor caído | `v2.0` |
| `diccionarios-controlador-rest` | Las URLs, status y JSON del controlador con `@WebMvcTest`, con un servicio falso | `v2.0` |
| `diccionarios-app-servidor` | Que arranca y sirve los idiomas; y **el servicio remoto cumple el contrato del servicio contra este servidor de verdad** | `v2.0` |
| `diccionarios-impl-bbdd` | El contrato del repositorio, contra H2 en memoria | `v3.0` |
| `cargador-de-bbdd-palabras` | Primera carga, huella sin cambios, versión nueva, solo el idioma que cambia, idioma que desaparece | `v3.0` |
| `diccionarios-web` (`npm test`) | El mapeador (los dos 404, las sugerencias del cuerpo), el adaptador con `HttpTestingController` y el caso de uso con un suministrador falso | `v4.0` |

Pruebas que pasa cada etapa con `./mvnw install`: 84 (`v1.0` y `v1.1`), 89 (`v1.2`), 120 (`v2.0`),
155 (`v3.0`), 166 (`v3.1` y `v4.0`, más las 11 del frontal).

## Novedades de Java 8+ en este código

Lo que usa el proyecto de cada versión. En el código, cada novedad está marcada la primera vez que
aparece en cada fichero con un comentario `// Java N: ...` que dice qué es. Aquí: la referencia
oficial (el JEP de OpenJDK que la trajo o, si no tiene, su Javadoc), la etapa en la que aparece por
primera vez y dónde mirar en esa rama.

| Versión | Novedad | Referencia | Desde | Dónde |
|---|---|---|---|---|
| Java 8 | Expresiones lambda | [JEP 126](https://openjdk.org/jeps/126) | `v1.0` | [BuscadorDePalabrasFactory.java:22](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/com/curso/diccionario/app/consola/factorias/BuscadorDePalabrasFactory.java#L22), [InterfazDeUsuarioFactory.java:20](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/com/curso/diccionario/app/consola/factorias/InterfazDeUsuarioFactory.java#L20), [SuministradorDeDiccionariosDesdeFicheros.java:99](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/SuministradorDeDiccionariosDesdeFicheros.java#L99), y 4 más |
| Java 8 | Referencias a método (`::`) | [JEP 126](https://openjdk.org/jeps/126) | `v1.0` | [CatalogoDeDiccionariosEnClasspath.java:45](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/CatalogoDeDiccionariosEnClasspath.java#L45) |
| Java 8 | Streams (`stream`, `map`, `filter`, `parallelStream`...) | [JEP 107](https://openjdk.org/jeps/107) | `v1.0` | [SuministradorDeDiccionariosDesdeFicheros.java:97](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/SuministradorDeDiccionariosDesdeFicheros.java#L97), [CatalogoDeDiccionariosEnClasspath.java:41](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/CatalogoDeDiccionariosEnClasspath.java#L41), [FormatoDeFicheroDeDiccionario.java:41](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/FormatoDeFicheroDeDiccionario.java#L41), y 1 más |
| Java 8 | `Optional` | [`Optional`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html) | `v1.0` | [Diccionario.java:16](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-api/src/main/java/com/curso/diccionario/api/Diccionario.java#L16), [SuministradorDeDiccionarios.java:17](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-api/src/main/java/com/curso/diccionario/api/SuministradorDeDiccionarios.java#L17), [ProcesadorDePeticiones.java:34](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/com/curso/diccionario/app/consola/ProcesadorDePeticiones.java#L34), y 6 más |
| Java 8 | Métodos `default` en interfaces | [JEP 126](https://openjdk.org/jeps/126) | `v1.2` | [Diccionario.java:22](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.2-repositorio-que-avisa-de-errores/proyectos/diccionario/diccionarios-api/src/main/java/com/curso/diccionario/api/Diccionario.java#L22), [SuministradorDeDiccionarios.java:19](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.2-repositorio-que-avisa-de-errores/proyectos/diccionario/diccionarios-api/src/main/java/com/curso/diccionario/api/SuministradorDeDiccionarios.java#L19) |
| Java 8 | `Map.computeIfAbsent` | [`Map.computeIfAbsent`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html#computeIfAbsent(K,java.util.function.Function)) | `v1.2` | [SuministradorDeDiccionariosDesdeFicheros.java:157](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.2-repositorio-que-avisa-de-errores/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/SuministradorDeDiccionariosDesdeFicheros.java#L157) |
| Java 9 | Módulos (`module-info.java`: `requires`, `exports`, `uses`, `provides`) | [JEP 261](https://openjdk.org/jeps/261) | `v1.0` | [module-info.java:2](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-api/src/main/java/module-info.java#L2), [module-info.java:2](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/module-info.java#L2), [module-info.java:2](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/module-info.java#L2), y 4 más |
| Java 9 | `ServiceLoader` sobre módulos (`uses` / `provides`) | [`ServiceLoader`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ServiceLoader.html) | `v1.0` | [BuscadorDePalabrasFactory.java:14](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/com/curso/diccionario/app/consola/factorias/BuscadorDePalabrasFactory.java#L14), [InterfazDeUsuarioFactory.java:12](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/com/curso/diccionario/app/consola/factorias/InterfazDeUsuarioFactory.java#L12), [SuministradorDeDiccionariosDesdeFicheros.java:63](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/SuministradorDeDiccionariosDesdeFicheros.java#L63), y 6 más |
| Java 9 | `List.of` / `Map.of` / `Set.of` | [JEP 269](https://openjdk.org/jeps/269) | `v1.0` | [InterfazDeUsuarioFactory.java:22](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/com/curso/diccionario/app/consola/factorias/InterfazDeUsuarioFactory.java#L22), [SuministradorDeDiccionariosDesdeFicheros.java:86](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/SuministradorDeDiccionariosDesdeFicheros.java#L86) |
| Java 9 | Métodos privados en interfaces | [JEP 213](https://openjdk.org/jeps/213) | `v3.1` | [PalabrasSimilares.java:47](https://github.com/IvanciniGT/formacionJava17-21/blob/v3.1-sugerencias/proyectos/diccionario/diccionarios-service-impl/src/main/java/com/curso/diccionario/service/impl/PalabrasSimilares.java#L47) |
| Java 9 | `@Deprecated(forRemoval = true)` | [JEP 277](https://openjdk.org/jeps/277) | `v1.2` | [Diccionario.java:19](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.2-repositorio-que-avisa-de-errores/proyectos/diccionario/diccionarios-api/src/main/java/com/curso/diccionario/api/Diccionario.java#L19), [SuministradorDeDiccionarios.java:14](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.2-repositorio-que-avisa-de-errores/proyectos/diccionario/diccionarios-api/src/main/java/com/curso/diccionario/api/SuministradorDeDiccionarios.java#L14) |
| Java 9 | `InputStream.readAllBytes()` | [`InputStream.readAllBytes`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/InputStream.html#readAllBytes()) | `v1.0` | [FormatoDeFicheroDeDiccionario.java:29](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/FormatoDeFicheroDeDiccionario.java#L29), [FicherosDePruebaTest.java:83](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/test/java/com/curso/diccionario/impl/ficheros/FicherosDePruebaTest.java#L83) |
| Java 10 | `List.copyOf` / `Map.copyOf` | [`List.copyOf`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection)) | `v1.0` | [DiccionarioDesdeFichero.java:39](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/DiccionarioDesdeFichero.java#L39), [PalabraEncontrada.java:26](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-service-api/src/main/java/com/curso/diccionario/service/api/PalabraEncontrada.java#L26), [InterfazDeUsuarioPorArgumentos.java:20](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/ui-impl-consola/src/main/java/com/curso/diccionario/ui/consola/argumentos/InterfazDeUsuarioPorArgumentos.java#L20), y 5 más |
| Java 11 | `java.net.http.HttpClient` | [JEP 321](https://openjdk.org/jeps/321) | `v2.0` | [ClienteDelServidorDeDiccionarios.java:36](https://github.com/IvanciniGT/formacionJava17-21/blob/v2.0-servidor-central/proyectos/diccionario/diccionarios-service-impl-rest/src/main/java/com/curso/diccionario/service/rest/ClienteDelServidorDeDiccionarios.java#L36), [ServidorArrancaTest.java:26](https://github.com/IvanciniGT/formacionJava17-21/blob/v2.0-servidor-central/proyectos/diccionario/diccionarios-app-servidor/src/test/java/com/curso/diccionario/app/servidor/ServidorArrancaTest.java#L26), [GeneradorDeEspecificacionOpenApi.java:38](https://github.com/IvanciniGT/formacionJava17-21/blob/v2.0-servidor-central/proyectos/diccionario/diccionarios-controlador-rest-api/src/test/java/com/curso/diccionario/controlador/rest/api/GeneradorDeEspecificacionOpenApi.java#L38) |
| Java 11 | Métodos nuevos de `String` (`strip`, `isBlank`, `lines`, `repeat`) | [`String`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html#strip()) | `v1.0` | [CatalogoDeDiccionariosEnClasspath.java:68](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/CatalogoDeDiccionariosEnClasspath.java#L68), [FormatoDeFicheroDeDiccionario.java:40](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/FormatoDeFicheroDeDiccionario.java#L40), [BuscadorDePalabrasEnDiccionarios.java:109](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-service-impl/src/main/java/com/curso/diccionario/service/impl/BuscadorDePalabrasEnDiccionarios.java#L109), y 2 más |
| Java 11 | `Path.of`, `Files.readString` / `writeString` | [`Path.of`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Path.html#of(java.lang.String,java.lang.String...)) | `v1.0` | [SuministradorDeDiccionariosDesdeFicheros.java:92](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/SuministradorDeDiccionariosDesdeFicheros.java#L92) |
| Java 14 | `switch` como expresión | [JEP 361](https://openjdk.org/jeps/361) | `v2.0` | [ControladorRestDiccionarios.java:72](https://github.com/IvanciniGT/formacionJava17-21/blob/v2.0-servidor-central/proyectos/diccionario/diccionarios-controlador-rest/src/main/java/com/curso/diccionario/controlador/rest/ControladorRestDiccionarios.java#L72) |
| Java 15 | Bloques de texto (`"""`) | [JEP 378](https://openjdk.org/jeps/378) | `v1.0` | [InterfazDeUsuarioPorArgumentos.java:59](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/ui-impl-consola/src/main/java/com/curso/diccionario/ui/consola/argumentos/InterfazDeUsuarioPorArgumentos.java#L59), [InterfazDeUsuarioPorArgumentosTest.java:61](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/ui-impl-consola/src/test/java/com/curso/diccionario/ui/consola/argumentos/InterfazDeUsuarioPorArgumentosTest.java#L61) |
| Java 16 | `record` | [JEP 395](https://openjdk.org/jeps/395) | `v1.0` | [SignificadoDesdeFichero.java:8](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/SignificadoDesdeFichero.java#L8), [IdiomaNoSoportado.java:8](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-service-api/src/main/java/com/curso/diccionario/service/api/IdiomaNoSoportado.java#L8), [PalabraEncontrada.java:17](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-service-api/src/main/java/com/curso/diccionario/service/api/PalabraEncontrada.java#L17), y 6 más |
| Java 16 | `Stream.toList()` | [`Stream.toList`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#toList()) | `v1.0` | [SuministradorDeDiccionariosDesdeFicheros.java:104](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/SuministradorDeDiccionariosDesdeFicheros.java#L104), [CatalogoDeDiccionariosEnClasspath.java:52](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/CatalogoDeDiccionariosEnClasspath.java#L52), [FormatoDeFicheroDeDiccionario.java:40](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-impl-ficheros/src/main/java/com/curso/diccionario/impl/ficheros/formato/FormatoDeFicheroDeDiccionario.java#L40) |
| Java 17 | Clases e interfaces selladas (`sealed` / `permits`) | [JEP 409](https://openjdk.org/jeps/409) | `v1.0` | [ResultadoDeBusqueda.java:75](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-service-api/src/main/java/com/curso/diccionario/service/api/ResultadoDeBusqueda.java#L75) |
| Java 17 | `HexFormat` | [`HexFormat`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HexFormat.html) | `v3.0` | [CargadorDeDiccionarios.java:91](https://github.com/IvanciniGT/formacionJava17-21/blob/v3.0-bbdd/proyectos/diccionario/cargador-de-bbdd-palabras/src/main/java/com/curso/diccionario/cargador/CargadorDeDiccionarios.java#L91) |
| Java 21 | `switch` con patrones | [JEP 441](https://openjdk.org/jeps/441) | `v1.0` | [ProcesadorDePeticiones.java:57](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/com/curso/diccionario/app/consola/ProcesadorDePeticiones.java#L57) |
| Java 21 | Patrones de `record` (*record patterns*) | [JEP 440](https://openjdk.org/jeps/440) | `v1.0` | [ProcesadorDePeticiones.java:59](https://github.com/IvanciniGT/formacionJava17-21/blob/v1.0-consola-ficheros/proyectos/diccionario/diccionarios-app-consola/src/main/java/com/curso/diccionario/app/consola/ProcesadorDePeticiones.java#L59) |

## Limitaciones conocidas

- **Diccionarios de mentira.** Las 5000 palabras de cada idioma son inventadas, y se nota en las
  sugerencias: `melonn` propone `melón`, pero también `mevon` o `molon`.
- **Sin acentos, si dos palabras se escriben igual** (*papa* y *papá*) y se busca *PAPA*, se
  encuentra la primera en orden alfabético. La exacta no tiene ese problema.
- **Hasta la `v1.2`, los errores no llegan a quien pregunta**: el repositorio 1.0.0 no tiene por dónde
  decirlo, y un fichero ilegible parece *"no existe"*.
- **El servidor con BBDD acepta peticiones mientras carga** (`v3.0`): el cargador corre con Tomcat ya
  escuchando, y los primeros segundos responde como si no hubiera idiomas. En Kubernetes lo cubre la
  *readiness probe* de Spring Boot.
- **Solo module path** para las apps de consola: en el classpath el `ServiceLoader` no usa los
  `provides` de `module-info`.
- **Las sugerencias y la búsqueda sin acentos recorren el diccionario entero.** Con 5000 palabras es
  instantáneo; con millones haría falta un índice.
