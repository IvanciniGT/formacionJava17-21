/**
 * Traduccion de la interfaz sellada `RespuestaPalabra` del backend.
 *
 * Los fallos NO son excepciones: son variantes del tipo de retorno. Quien consuma esto esta
 * OBLIGADO por el compilador a contemplar los tres casos.
 *
 * Fijate en la diferencia con el DTO: alli todo era opcional y las tres situaciones cabian en
 * un mismo registro plano. Aqui cada variante lleva EXACTAMENTE lo que necesita y nada mas.
 * No existe una `PalabraEncontrada` sin significados, ni una `NoEncontrada` con ellos.
 */
export type RespuestaPalabra =
  | PalabraEncontrada
  | PalabraNoEncontrada
  | ErrorAlObtenerPalabra;

/** El equivalente a la interfaz `Significado` del backend: un texto y sus ejemplos de uso. */
export interface Significado {
  readonly texto: string;
  /** Puede venir vacia: no todos los significados tienen ejemplos. */
  readonly ejemplos: readonly string[];
}

export interface PalabraEncontrada {
  readonly tipo: 'palabra-encontrada';
  readonly palabra: string;
  readonly significados: readonly Significado[];
}

export interface PalabraNoEncontrada {
  readonly tipo: 'palabra-no-encontrada';
  readonly palabra: string;
  /** Puede venir vacia: "he buscado parecidas y no hay" NO es lo mismo que "no he mirado". */
  readonly sugerencias: readonly string[];
}

export interface ErrorAlObtenerPalabra {
  readonly tipo: 'error-al-obtener-palabra';
  readonly mensaje: string;
}

export function palabraEncontrada(palabra: string, significados: readonly Significado[]): PalabraEncontrada {
  return { tipo: 'palabra-encontrada', palabra, significados };
}

export function palabraNoEncontrada(palabra: string, sugerencias: readonly string[]): PalabraNoEncontrada {
  return { tipo: 'palabra-no-encontrada', palabra, sugerencias };
}

export function errorAlObtenerPalabra(mensaje: string): ErrorAlObtenerPalabra {
  return { tipo: 'error-al-obtener-palabra', mensaje };
}
