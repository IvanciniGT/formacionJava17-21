# Arquitectura de diccionarios-web

**Clean Architecture**, traducida del backend Java de este mismo proyecto. No es un diseño
nuevo: es el mismo, en otro lenguaje. `SuministradorDeDiccionarios` es aquí también el puerto,
y el servidor central (`diccionario-app-servidor`) es solo otra implementación más detrás.

## La regla de dependencia

Las flechas apuntan **siempre hacia dentro**. Una capa jamás importa nada de una capa más
externa.

```
  infraestructura  ──▶  adaptadores  ──▶  casos-de-uso  ──▶  entidades
      (Angular)          (HTTP, DTO)      (puertos, flujo)     (modelo)
```

`entidades/` no importa nada. Ni Angular, ni RxJS, ni `HttpClient`. Si un fichero de esa
carpeta necesita un import del framework, está en la capa equivocada.

## Estructura y primer desglose de componentes

> **Primera pasada.** Los nombres de fichero siguen la guía de estilo de Angular 20+, que ya
> no usa el sufijo `.component`. Pendiente de análisis más exhaustivo.

```
src/app/

  entidades/                                   MODELO INTERNO. No importa NADA.
    idioma.ts                                    { codigo } y nada mas: el nombre es presentacion
    palabra.ts                                   { texto }
    respuesta-palabra.ts                         unión sellada de 3 variantes
    respuesta-idiomas.ts                         unión sellada de 2 variantes
    caso-no-contemplado.ts                       el "sealed" del compilador

  casos-de-uso/
    servicios/api/
      suministrador-de-diccionarios.ts           PUERTO (clase abstracta)
    buscar-palabra.ts                            orquesta la búsqueda, expone signals
    listar-idiomas-disponibles.ts                pobla el selector

  adaptadores/
    servicios/impl/
      suministrador-de-diccionarios-rest.ts      implementa el puerto con HttpClient
    dto/                                         forma exacta del contrato OpenAPI
      contrato.ts                                generado: npm run contrato
      dtos.ts                                    alias con nombre de los tipos del contrato
    mapeadores/
      respuesta-palabra.mapeador.ts              DTO + estado HTTP -> variante de la unión
      idioma.mapeador.ts

  infraestructura/
    tema/
      servicio-de-tema.ts                        claro / oscuro / sistema, persistido
      selector-de-tema/                          los tres botones

    ui/
      layout/
        layout.ts                                cabecera + buscador + <router-outlet>
        cabecera/                                título de la aplicación

      buscador/                                  SIEMPRE VISIBLE, vive en el layout
        buscador.ts                                orquesta; no pinta nada por sí mismo
        selector-de-idioma/
          selector-de-idioma.ts                    desplegable de idiomas disponibles
        campo-de-palabra/
          campo-de-palabra.ts                      entrada de texto y envío

      resultado/                                 RUTA /:idioma/:palabra
        resultado.ts                               lee la ruta y elige qué pintar
        cabecera-de-resultado/                     palabra buscada + bandera + idioma
        lista-de-significados/                     caso "palabra encontrada"
        lista-de-sugerencias/                      no encontrada CON parecidas; cada una, enlace
        sin-sugerencias/                           no encontrada SIN parecidas

      bienvenida/                                RUTA /  (aún no se ha buscado nada)
        bienvenida.ts                              qué es esto y cómo se usa

      comunes/
        bandera-de-idioma/                         átomo reutilizable: código -> <img>
        cargando/                                  indicador de espera
        mensaje-de-error/                          caso "error" de las uniones

    idiomas/
      catalogo-de-idiomas.ts                     código -> { nombre, bandera }. PRESENTACIÓN.

public/
  banderas/  es.png  en.png  fr.png  it.png     generadas en el repo, dominio público

  app.routes.ts                                  '' -> bienvenida ; ':idioma/:palabra' -> resultado
  app.config.ts                                  RAÍZ DE COMPOSICIÓN
```

### Por qué el buscador vive en el layout y no en cada página

Si el formulario estuviera dentro de cada ruta habría que duplicarlo, o remontarlo en cada
navegación perdiendo lo escrito. Dejándolo en el layout, **la URL manda sobre el resultado y
el formulario sobrevive a la navegación**: pinchar una sugerencia cambia la ruta, se repinta
el resultado y el buscador sigue donde estaba, con el idioma elegido.

### Los tres estados de la unión, un componente cada uno

`resultado.ts` no dibuja: hace el `switch` exhaustivo sobre `RespuestaPalabra` y delega.

| Variante | Componente |
|---|---|
| `palabra-encontrada` | `lista-de-significados/` |
| `palabra-no-encontrada` | `lista-de-sugerencias/` |
| `error` | `comunes/mensaje-de-error/` |
| *(en vuelo)* | `comunes/cargando/` |

Así **la unión sellada se ve en la pantalla**: cada caso del tipo tiene su componente, y añadir
una variante obliga a añadir su vista o el proyecto no compila.

### Decisiones del análisis detallado

**El nombre visible del idioma sale de un catálogo del frontal**, no del backend ni de
`Intl.DisplayNames`: un único sitio con código, nombre y bandera juntos. Vive en
`infraestructura/idiomas/`, **no en `entidades/`**, porque cómo se llama un idioma de cara al
usuario es presentación, no dominio. Contrapartida asumida: cuando el backend publique un
diccionario nuevo hay que añadirlo al catálogo. Debe degradar con elegancia y mostrar el
código si llega uno desconocido.

**"No hay palabras parecidas" tiene componente propio**, `sin-sugerencias/`, pero **no una
variante nueva en la unión**. `resultado.ts` decide entre `lista-de-sugerencias` y
`sin-sugerencias` según la lista venga o no vacía. El tipo se queda en tres variantes: es la
misma prudencia que está escrita en `Diccionario.java`, no acabar con cuarenta clases de
respuesta para complicarle la vida a quien consume el API.

**La URL es la fuente de la verdad del estado de búsqueda.** `resultado.ts` recibe `idioma` y
`palabra` como `input()` de señal desde la ruta, y eso dispara el caso de uso. **Buscar es
navegar.** No hay dos sitios que puedan discrepar, y atrás, adelante, recargar y compartir un
enlace funcionan sin escribir una línea para ello. Es el principio de fuente única de la
verdad que el proyecto aplica en todas partes.

**Banderas en PNG, no emoji.** Windows no trae los glifos de bandera: 🇫🇷 se pinta allí como
las letras `FR`. Con imágenes se ve igual en todas partes. Se generan en el repositorio y se
versionan en `public/banderas/`; los diseños de banderas nacionales son de dominio público.

**Accesibilidad de la bandera:** `alt` con el nombre del idioma cuando la bandera va sola, y
`alt=""` cuando el nombre ya está escrito al lado, para que el lector de pantalla no lo repita.
El significado nunca depende del icono.

**Zoneless con `provideZonelessChangeDetection()`, y `OnPush` en todos los componentes.** Son
las señales las que avisan de qué ha cambiado, en vez de parchear el navegador para adivinarlo.
Menos trabajo por render, un polyfill menos, y se explica solo: el código dice qué cambió,
nadie lo espía.

### Por qué `api` e `impl` están separados

Es la separación `diccionarios-api` / `diccionarios-impl-*` del backend. El
nombre se conserva; la ubicación la dicta la regla de dependencia: **el puerto pertenece a
quien lo consume** (el caso de uso), y la implementación vive fuera, en adaptadores. Si el
`impl` estuviera dentro de `casos-de-uso`, esa capa dependería de `HttpClient` y la
arquitectura sería decorativa.

## Modelos internos y DTOs son cosas distintas

**Nunca se usa un DTO fuera de `adaptadores/`.** El DTO es la forma que el backend ha
decidido darle a sus bytes; la entidad es lo que a esta aplicación le conviene manejar. Que
hoy se parezcan no los hace lo mismo: cuando el backend cambie, el daño se queda en el
mapeador.

TypeScript tiene tipado estructural, así que **nada impide** colar un DTO donde se espera una
entidad. Es una regla de disciplina, no algo que el compilador vaya a atrapar.

### Los DTO no se escriben a mano

`contrato.ts` lo genera `openapi-typescript` (`npm run contrato`) desde la especificación que
springdoc saca de `ApiRestDiccionarios`, la interfaz Java del contrato REST. Es el mismo DRY que
en el backend: el contrato se escribe **una vez**, en Java, y de ahí salen el servidor, el
cliente Java y los tipos de este frontal. Si el backend cambia un campo, se regenera y el
compilador señala cada sitio que deja de cuadrar.

### Qué traduce el mapeador

El servidor ya separa bien los casos: 200 con la palabra, y dos 404 que **solo distingue el
campo `codigo`** del cuerpo (`IDIOMA_NO_DISPONIBLE` o `PALABRA_NO_ENCONTRADA`). La entidad es
una unión donde **cada variante lleva exactamente lo que necesita**:

```typescript
export type RespuestaPalabra =
  | { readonly tipo: 'palabra-encontrada';    palabra: string; significados: Significado[] }
  | { readonly tipo: 'palabra-no-encontrada'; palabra: string; sugerencias:  string[] }
  | { readonly tipo: 'error-al-obtener-palabra'; mensaje: string };
```

Y una traducción que solo existe aquí: el servidor habla de `ES` y este frontal de `es` (las
rutas `/es/casa`, las banderas). Vive en `idioma.mapeador.ts` y en ningún sitio más.

## Los errores son valores, también con RxJS

RxJS tiene canal de error propio, que es el equivalente a las excepciones que el backend
descartó a conciencia. Para conservar el diseño, **el adaptador no deja pasar nada por ese
canal**: `catchError` convierte el fallo en un valor de la unión.

```typescript
dameSignificados(idioma: string, palabra: string): Observable<RespuestaPalabra> {
  return this.http.get<PalabraDto>(url, { params: { ignorarMayusculasYAcentos: true } }).pipe(
    map(aPalabraEncontrada),
    catchError((e: HttpErrorResponse) => of(aRespuestaDeError(palabra, e))),
  );
}
```

**El `Observable<RespuestaPalabra>` nunca falla: falla dentro del tipo.** El consumidor no
necesita el segundo argumento de `subscribe`, y el `switch` exhaustivo manda.

El caso que lo justifica: el **404 de una palabra inexistente trae las sugerencias en el
cuerpo**, dentro de `HttpErrorResponse.error`. Un adaptador que solo mire el código de estado
pierde la funcionalidad entera; uno que no mire el `codigo` confunde la palabra que no existe con
el idioma que no existe.

## Exhaustividad: el `sealed` de TypeScript

```typescript
export function casoNoContemplado(valor: never): never {
  throw new Error(`Caso no contemplado: ${JSON.stringify(valor)}`);
}
```

Todo `switch` sobre una unión termina con `default: casoNoContemplado(respuesta);`. Si mañana
se añade una variante y algún `switch` no la trata, **deja de compilar**. Es lo mismo que
consigue el backend con `sealed` y el `switch` con patrones de Java 21 en `ProcesadorDePeticiones`.

## Componentes: pequeños, y partidos sin miedo

Un componente que además formatea, además llama a un servicio y además guarda estado tiene
tres responsabilidades. Se parte.

```
buscador/
  buscador.ts                    orquesta y nada más
  selector-de-idioma/            lista + bandera + selección
  campo-de-palabra/              entrada de texto y envío
resultado/
  resultado.ts
  lista-de-significados/
  lista-de-sugerencias/          cada sugerencia, un enlace
  cabecera-de-idioma/            bandera y nombre
```

Ninguno inyecta `HttpClient`. Hablan con casos de uso.

## Raíz de composición

El único fichero que conoce clases concretas. Es el equivalente a `BuscarPalabra` de la app
de consola, que monta las piezas con el `ServiceLoader`:

```typescript
providers: [
  { provide: SuministradorDeDiccionarios, useClass: SuministradorDeDiccionariosRest },
]
```

El puerto se declara como **clase abstracta**, que en Angular sirve a la vez de tipo y de token
de inyección. Cambiar de implementación es cambiar esta línea.

## Tests

Dos, elegidos porque demuestran para qué sirve todo lo anterior:

1. **Del mapeador y el adaptador:** que un 404 con sugerencias en el cuerpo se convierte en
   `palabra-no-encontrada` con sus sugerencias, y no en un error; y que el 404 de idioma, sí.
2. **De componente con un doble:** se inyecta un `SuministradorDeDiccionariosFalso` en lugar
   del real y **no hace falta mockear HTTP**. Ese es el premio de depender de abstracciones.

Estructura Dado / Cuando / Entonces, con el requisito enunciado antes que el código, como en
el backend.
