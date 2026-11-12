import type { Idioma } from './idioma';

/**
 * Traduccion de `RespuestaIdiomas` del backend.
 *
 * Por que no devolver simplemente `Idioma[]`? Porque entonces, si el servidor no responde,
 * solo se podria devolver una lista vacia. Y "no hay diccionarios" NO ES LO MISMO que
 * "no he podido averiguar que diccionarios hay". Se perderia esa informacion.
 */
export type RespuestaIdiomas = IdiomasDisponibles | ErrorAlObtenerIdiomas;

export interface IdiomasDisponibles {
  readonly tipo: 'idiomas-disponibles';
  readonly idiomas: readonly Idioma[];
}

export interface ErrorAlObtenerIdiomas {
  readonly tipo: 'error-al-obtener-idiomas';
  readonly mensaje: string;
}

export function idiomasDisponibles(idiomas: readonly Idioma[]): IdiomasDisponibles {
  return { tipo: 'idiomas-disponibles', idiomas };
}

export function errorAlObtenerIdiomas(mensaje: string): ErrorAlObtenerIdiomas {
  return { tipo: 'error-al-obtener-idiomas', mensaje };
}
