# Contrato de los ficheros de diccionario

Lo que tiene que cumplir un fichero de diccionario para que `diccionarios-impl-ficheros` lo lea (y,
desde la `v3.0`, para que `cargador-de-bbdd-palabras` lo cargue en la BBDD, que usa este mismo
lector). No es un interfaz Java, pero es igual de contrato: los diccionarios los escriben
lingüistas, en proyectos aparte (`diccionario-es`, `diccionario-en`...), y este documento es lo que
tienen que respetar.

El código que lo implementa: `FormatoDeFicheroDeDiccionario` (el contenido de cada fichero) y
`CatalogoDeDiccionariosEnClasspath` (dónde están y qué idiomas hay), en el paquete
`com.curso.diccionario.impl.ficheros.formato`.

## Dónde van

Cada idioma es un jar con dos ficheros en `META-INF/diccionarios/`:

| Fichero | Qué es |
|---|---|
| `<idioma>.txt` | El diccionario. El nombre, en minúsculas: `es.txt`, `en.txt` |
| `idiomas` | El índice: el código del idioma (`ES`), uno por línea |

- **El índice es lo que hace que el idioma exista.** Dentro de un jar no se puede listar una carpeta;
  `ClassLoader.getResources("META-INF/diccionarios/idiomas")` (en plural) devuelve los índices de
  **todos** los jars, y los idiomas disponibles son, por construcción, los jars que haya.
- El código del índice da igual en mayúsculas o en minúsculas: se usa en mayúsculas (`ES`).
- Un idioma en el índice sin su `.txt` se ignora: es un jar mal hecho.
- En `META-INF` y no en un paquete: varios jars pueden tener la misma carpeta sin que sea un paquete
  partido entre módulos, y en el module path no hace falta `opens`.

Fuera de los jars, con la variable de entorno `DICCIONARIOS_CARPETA=/ruta/`, se leen los
`<idioma>.txt` de esa carpeta del disco. Ahí no hay índice: los idiomas son los ficheros `.txt` con
un solo punto en el nombre (`es.prueba.txt` no es el idioma `ES.PRUEBA`).

## Qué contienen

- **Codificación: UTF-8.** Sin BOM.
- **Una palabra por línea:**

  ```
  palabra=significado1(ejemplo1)(ejemplo2)|significado2(ejemplo1)|significado3
  ```

  ```
  melón=Fruto grande, redondo y de pulpa jugosa y dulce.(Me gusta comer melón!)|Persona con pocas luces.(Eres un melón)(No seas melón)
  pera=Fruto del peral.
  ```

- **La palabra**, lo que va antes del `=`, **tal y como se escribe**: con sus acentos y en minúsculas
  (salvo que sea un nombre propio). Sin espacios alrededor del `=`: formarían parte de la palabra.
  La búsqueda exacta la compara tal cual; la que ignora mayúsculas y acentos la normaliza el servicio,
  no el fichero.
- **Los significados**, detrás del `=`, separados por `|`. **Al menos uno.** El orden importa: es el
  orden en que se enseñan.
- **Los ejemplos** de cada significado, detrás de su texto, cada uno entre paréntesis: `(...)`. Pueden
  no tener ninguno. Los espacios alrededor del texto del significado se quitan.
- **Las líneas en blanco se ignoran.** No hay comentarios.

### Caracteres reservados

`=`, `|`, `(` y `)` separan las partes y **el formato no los escapa**: no pueden aparecer dentro de
una palabra, un significado o un ejemplo. Un `=` de más se come lo que va detrás.

## Qué pasa si no se cumple

El lector no se salta las líneas que no entiende: **un error de formato hace fallar la carga del
fichero entero**, y ese idioma no se puede usar hasta que se arregle. Es a propósito: un diccionario
a medio leer daría resultados que nadie podría explicar.

| Error | Qué pasa |
|---|---|
| Una línea sin `=` | Falla la carga del fichero |
| Una palabra sin significados (`palabra=`) | Falla la carga del fichero |
| La misma palabra en dos líneas | Falla la carga del fichero (clave duplicada) |
| Un `=`, `|`, `(` o `)` dentro de un texto | No falla, pero el texto se parte por donde no debe |
| Un índice que anuncia un idioma sin su `.txt` | Ese idioma no aparece |

## Cómo se versiona

Cada diccionario versiona por su cuenta (control de configuración, `v1.1`):

- Palabras nuevas: **minor** (`diccionario-es` 1.0.0 → 1.1.0).
- Erratas en definiciones o ejemplos: **patch** (`diccionario-en` 1.0.0 → 1.0.1).
- Un cambio en este formato rompería todos los diccionarios: sería un **major** del formato, y de
  `diccionarios-impl-ficheros`.
