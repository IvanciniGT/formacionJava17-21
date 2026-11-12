# diccionarios-web

El frontal web del diccionario, en Angular 21. Es **otro frontal más**, igual que la app de
consola: habla con el mismo servidor central (`diccionario-app-servidor`) por su API REST, y el
servidor no sabe ni le importa quién le pregunta.

```bash
# 1. El servidor (desde proyectos/diccionario)
mvn install -DskipTests
cd diccionario-app-servidor && mvn spring-boot:run      # o -Pbbdd para la BBDD

# 2. El frontal (desde proyectos/diccionario/diccionarios-web)
npm install
npm start                                              # http://localhost:4200
```

El Angular CLI reenvía `/api` al servidor (`proxy.conf.json`, puerto 8080), así que en
desarrollo no hace falta configurar CORS.

## Qué hace

- Selector del idioma, poblado con **los diccionarios que el servidor tenga** (no una lista
  fija escrita en el frontal).
- Campo de búsqueda de la palabra.
- El resultado tiene su URL (`/es/melón`): se puede compartir y el botón "atrás" funciona.
- Cada significado, con sus ejemplos.
- **Sin mayúsculas ni acentos**: "Atun" encuentra "atún" (la búsqueda del servicio que los ignora).
- **Sugerencias**: si la palabra no existe, se enseñan sin que el usuario haga nada las palabras
  parecidas que devuelve el servidor (las calcula el servicio del servidor, el mismo que usa la consola), y cada una
  es un enlace a su resultado.
- Tema claro, oscuro o el del sistema (por defecto), recordado entre visitas.
- Estados distinguibles: cargando, sin resultados, sin sugerencias, error del servidor.

## Cómo habla con el servidor

| Petición | Para qué |
|---|---|
| `GET /api/v1/diccionarios` | Los idiomas del selector |
| `GET /api/v1/diccionarios/{idioma}/palabras/{palabra}?ignorarMayusculasYAcentos=true` | Los significados ("Atun" encuentra "atún"); si no está, 404 `PALABRA_NO_ENCONTRADA` **con las sugerencias en el cuerpo** |

Una sola petición por búsqueda. Los dos 404 del servidor (idioma o palabra) **solo se distinguen
por el campo `codigo`** del cuerpo, y el de palabra trae `sugerencias`. Con `HttpClient` ese cuerpo
llega en `HttpErrorResponse.error`: un adaptador que solo mirase el status perdería las
sugerencias enteras.

## El contrato no se escribe a mano

`src/app/adaptadores/dto/contrato.ts` lo genera `openapi-typescript` desde la especificación
OpenAPI que springdoc saca de `ApiRestDiccionarios` (el interfaz Java del contrato REST):

```bash
mvn package -pl diccionarios-controlador-rest-api   # desde proyectos/diccionario: deja target/openapi.yaml
npm run contrato                                    # desde aquí: regenera contrato.ts
```

El contrato se escribe una vez, en Java, y de ahí salen el servidor, el cliente Java y los
tipos de este frontal. Si el backend cambia un campo, se regenera y el compilador señala cada
sitio de aquí que deja de cuadrar.

## Arquitectura

**Clean Architecture**, la misma del backend en otro lenguaje. Detalle en
[ARQUITECTURA.md](ARQUITECTURA.md). Las dos reglas que no se negocian:

1. **Ningún componente inyecta `HttpClient`.** Habla con un puerto (`SuministradorDeDiccionarios`,
   como en Java).
2. **El dominio no importa nada de Angular.**

Capas: `entidades` ← `casos-de-uso` ← `adaptadores` ← `infraestructura`, con las dependencias
apuntando siempre hacia dentro. Los fallos son variantes de una unión discriminada con `switch`
exhaustivo (el `sealed` de TypeScript), nunca excepciones ni `null`.

Para pasar de otro backend a este solo cambió `adaptadores/` (DTO, mapeadores y el adaptador
REST), más un detalle de dominio: `Significado` con sus ejemplos, porque este servidor los manda.

## Convenciones

- **HTML y CSS en ficheros aparte**: `templateUrl` y `styleUrl` siempre.
- **Zoneless y `OnPush`** en todos los componentes.
- Componentes standalone, signals, `@if`/`@for`, `inject()`.

## Pruebas

```bash
npm test
```

- **Mapeador**: los dos 404, el fallo de red, los ejemplos de cada significado, `ES` ↔ `es`.
- **Adaptador** (con `HttpTestingController`): la petición que sale de verdad (la búsqueda que
  ignora mayúsculas y acentos, con `ES` y no `es`), y que las sugerencias del 404 llegan al
  dominio sin ninguna petición de más.
- **Caso de uso**, con un suministrador falso: sin tocar HTTP. Ese es el premio de depender de
  abstracciones.
