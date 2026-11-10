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

Según avancemos irán apareciendo carpetas con los proyectos de ejemplo.
