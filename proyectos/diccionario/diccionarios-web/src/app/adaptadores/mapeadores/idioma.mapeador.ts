import type { Idioma } from '../../entidades/idioma';
import type { CodigosDeIdiomaDto } from '../dto/dtos';

/**
 * El servidor habla de "ES"; este frontal, de "es" (las rutas /es/casa, el catalogo de
 * nombres y banderas). La traduccion vive aqui y en `codigoDelServidor`, y en ningun sitio
 * mas: es el unico punto a tocar si el backend cambia su forma de nombrar los idiomas.
 *
 * Los codigos vacios se descartan: una entidad sin codigo no significa nada.
 */
export function aIdiomas(dtos: CodigosDeIdiomaDto): Idioma[] {
  return dtos.filter(codigo => !!codigo).map(codigo => ({ codigo: codigo.toLowerCase() }));
}

export function codigoDelServidor(codigo: string): string {
  return codigo.toUpperCase();
}
