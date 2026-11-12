import { HttpErrorResponse } from '@angular/common/http';

import {
  errorAlObtenerPalabra,
  palabraEncontrada,
  palabraNoEncontrada,
  type RespuestaPalabra,
} from '../../entidades/respuesta-palabra';
import type { ErrorDto, PalabraDto } from '../dto/dtos';

/**
 * Convierte lo que llega por HTTP en una variante de la union del dominio.
 *
 * El servidor ya distingue los casos con el status y el campo `codigo` del error: aqui solo
 * se traducen.
 */

/** Respuesta 200: la palabra existe y trae sus significados, cada uno con sus ejemplos. */
export function aPalabraEncontrada(dto: PalabraDto): RespuestaPalabra {
  return palabraEncontrada(
    dto.palabra,
    dto.significados.map(significado => ({ texto: significado.texto, ejemplos: significado.ejemplos ?? [] })),
  );
}

/**
 * Cualquier respuesta de error.
 *
 * ESTE ES EL PUNTO CRITICO: hay dos 404 y SOLO el campo `codigo` los distingue. Y el de palabra no
 * encontrada trae las sugerencias EN EL CUERPO, que con HttpClient llega en `HttpErrorResponse.error`.
 * Un adaptador que solo mirase el status confundiria "no hay diccionario de elfico" con "esa
 * palabra no existe", y perderia las sugerencias enteras.
 */
export function aRespuestaDeError(palabra: string, e: HttpErrorResponse): RespuestaPalabra {
  const cuerpo = e.error as ErrorDto | null;
  if (e.status === 404 && cuerpo?.codigo === 'PALABRA_NO_ENCONTRADA') {
    return palabraNoEncontrada(palabra, cuerpo.sugerencias ?? []);
  }
  if (e.status === 404 && cuerpo?.codigo === 'IDIOMA_NO_DISPONIBLE') {
    return errorAlObtenerPalabra('No hay diccionario para ese idioma.');
  }
  return errorAlObtenerPalabra(mensajeDeRed(e));
}

export function mensajeDeRed(e: HttpErrorResponse): string {
  // El `mensaje` del servidor no se enseña: dice el propio contrato que no es para el usuario final.
  return e.status === 0
    ? 'No se ha podido contactar con el servidor de diccionarios.'
    : `El servidor de diccionarios ha respondido con un error (${e.status}).`;
}
