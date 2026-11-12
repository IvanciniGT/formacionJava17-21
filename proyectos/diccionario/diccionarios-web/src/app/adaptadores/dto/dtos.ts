import type { components } from './contrato';

/**
 * Lo que viaja por HTTP, con los nombres del contrato OpenAPI del servidor.
 *
 * Ninguno se escribe a mano: salen de contrato.ts, que se genera (`npm run contrato`) desde
 * la especificacion que springdoc saca de ApiRestDiccionarios. Si el backend cambia un campo,
 * se regenera y el compilador señala cada sitio de este frontal que deja de cuadrar.
 *
 * De adaptadores/ para dentro no se viaja con estos tipos, sino con entidades del dominio.
 */
export type PalabraDto = components['schemas']['PalabraDTO'];
export type SignificadoDto = components['schemas']['SignificadoDTO'];
export type ErrorDto = components['schemas']['ErrorDTO'];

/** `GET /api/v1/diccionarios` y `.../similares` devuelven listas de textos, sin esquema propio. */
export type CodigosDeIdiomaDto = string[];
export type PalabrasSimilaresDto = string[];
