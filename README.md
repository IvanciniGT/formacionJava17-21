# Java LTS: de Java 8 a Java 21

Aquí va a estar todo lo que hagamos en clase: las notas de cada día y el código
de los ejemplos. Lo iré subiendo según avancemos, así que **actualízalo al
empezar cada sesión**.

---

## De qué va el curso

Recorremos las versiones LTS de Java —**8, 11, 17 y 21**— y lo que cada una
cambió en la forma de escribir código. Las dos últimas se llevan la mayor parte
del tiempo: son las que se están usando en proyectos nuevos y las que más
cambian el día a día.

| Versión | Lo que veremos, a grandes rasgos |
|---|---|
| **Java 8** | Lambdas, interfaces funcionales, *streams*, `Optional`, la API de fechas `java.time` |
| **Java 11** | Módulos, `var`, `HttpClient`, métodos nuevos de `String` y colecciones, `jshell` |
| **Java 17** | `record`, clases `sealed`, *switch* como expresión, *pattern matching* con `instanceof`, bloques de texto |
| **Java 21** | *Pattern matching* en `switch`, *record patterns*, colecciones secuenciadas, hilos virtuales |

Java 8 y 11 los vemos como punto de partida: mucho de lo que llega en 17 y 21
solo se entiende bien sabiendo de dónde viene.

---

## Qué necesitas instalar

1. **JDK 21** (o posterior). Cualquier distribución vale; si no tienes
   preferencia, [Eclipse Temurin](https://adoptium.net/).
   Con un JDK moderno puedes compilar también para versiones anteriores, así que
   no hace falta tener instalados el 8, el 11 y el 17.
2. **Maven**. Es lo que usaremos para construir los proyectos.
3. **Un IDE**: IntelliJ IDEA, Eclipse o VS Code con las extensiones de Java.
   El que uses habitualmente.
4. **Git**, para descargarte este repositorio.

Para comprobar que todo está en su sitio:

```bash
java -version     # debe decir 21 o superior
javac -version
mvn -version
git --version
```

---

## Cómo descargarte el material

La primera vez:

```bash
git clone https://github.com/IvanciniGT/formacionJava17-21.git
cd formacionJava17-21
```

Cada día, antes de empezar:

```bash
git pull
```

Si has hecho cambios en tus copias de los ficheros y `git pull` se queja,
lo más sencillo es trabajar en una carpeta tuya fuera del repositorio y usar
este solo para consultar.

---

## Qué hay en cada carpeta

```
notas/      las notas de cada día de clase (dia1.md, dia2.md, ...)
```

El código del proyecto del curso no está en esta rama, sino en las suyas (ver abajo).

---

## El proyecto del diccionario, rama a rama

Durante el curso trabajamos sobre un mismo proyecto: buscar una palabra en el diccionario de un
idioma. Empieza siendo una app de consola con ficheros y acaba con un servidor central, una base
de datos y un frontal web, **sin tener que reescribir lo que ya funcionaba**: cada paso añade
piezas nuevas y toca lo mínimo.

Las piezas: el **repositorio** de diccionarios (lógica de persistencia, casi un SELECT), el
**servicio** de búsqueda `BuscadorDePalabras` (lógica de negocio), la **interfaz de usuario**, los
**diccionarios** de cada idioma y la **aplicación**, que lo ensambla todo.

Cada paso está en su propia rama. Todas compilan, pasan sus pruebas y funcionan:

| Rama | Qué trae | Java que se ve ahí |
|---|---|---|
| `v1.0-consola-ficheros` | La consola con ficheros, ya en componentes: repositorio, servicio con su resultado sellado, interfaz de usuario, un módulo por diccionario, y la aplicación con sus factorías | `Optional`, *streams* y lambdas, módulos (`module-info`), `ServiceLoader`, `record`, `sealed`, `switch` con patrones (Java 21), *text blocks* |
| `v1.1-control-de-configuracion` | Palabras nuevas, erratas corregidas y otros mensajes de ayuda: cada cambio en su componente, y una versión nueva de la aplicación que los lleva | (ninguno: es control de configuración) |
| `v1.2-repositorio-que-avisa-de-errores` | El repositorio aprende a avisar de los errores sin romper a nadie; el servicio lo absorbe y la consola ni se entera | métodos `default`, `@Deprecated(forRemoval)` |
| `v2.0-servidor-central` | El servicio se va a un servidor y lo expone un controlador REST. Solo componentes nuevos: no se modifica nada de lo que había | `java.net.http.HttpClient`; las factorías convertidas en `@Bean` de Spring |
| `v3.0-bbdd` | El servidor guarda los diccionarios en una base de datos, y un cargador la llena al arrancar | Spring Data JPA, transacciones |
| `v3.1-sugerencias` | Si la palabra no existe, el servicio sugiere las parecidas, sin romper su API | `record` local, `parallelStream` |
| `v4.0-frontal-web` | Un frontal web en Angular contra el mismo servidor, en paralelo con las apps de consola | (TypeScript: la misma arquitectura en otro lenguaje) |

Cada rama tiene en `proyectos/diccionario/README.md` qué trae, cómo se ejecuta y qué cambia
respecto a la anterior.

### Cómo verlas

La forma más cómoda es sacar cada rama en su propia carpeta, al lado de este repositorio, sin
tocar la de las notas:

```bash
git fetch
git worktree add ../diccionario-v1.0 v1.0-consola-ficheros
git worktree add ../diccionario-v2.0 v2.0-servidor-central
```

O cambiar de rama en esta misma carpeta, y volver a `main` para las notas:

```bash
git switch v1.0-consola-ficheros
git switch main
```

Y para ver qué cambió de un paso al siguiente:

```bash
git diff v1.0-consola-ficheros v1.1-control-de-configuracion --stat
git diff v1.1-control-de-configuracion v1.2-repositorio-que-avisa-de-errores -- proyectos/diccionario/diccionarios-api
```

Para compilar y ejecutar no hace falta tener Maven instalado: cada rama lleva el Maven wrapper
(`./mvnw`, o `mvnw.cmd` en Windows).
