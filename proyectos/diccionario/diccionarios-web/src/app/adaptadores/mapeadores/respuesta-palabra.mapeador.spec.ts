import { HttpErrorResponse } from '@angular/common/http';

import { aIdiomas, codigoDelServidor } from './idioma.mapeador';
import { aPalabraEncontrada, aRespuestaDeError } from './respuesta-palabra.mapeador';

/**
 * REQUISITO: cuando la palabra no existe, el servidor responde 404 y manda las palabras parecidas
 * EN EL CUERPO de esa respuesta de error. Y hay otro 404, el de idioma, que no hay que confundir.
 */
describe('Mapeador de respuesta de palabra', () => {
  it('conserva las sugerencias que viajan en el cuerpo de un 404', () => {
    // Dado el 404 de palabra no encontrada, con sugerencias
    const error = new HttpErrorResponse({
      status: 404,
      error: { codigo: 'PALABRA_NO_ENCONTRADA', mensaje: 'No está', sugerencias: ['mañana', 'manzana'] },
    });

    // Cuando lo mapeamos al dominio
    const respuesta = aRespuestaDeError('manana', error);

    // Entonces es una palabra no encontrada Y conserva las sugerencias
    expect(respuesta).toEqual({ tipo: 'palabra-no-encontrada', palabra: 'manana', sugerencias: ['mañana', 'manzana'] });
  });

  it('sin el campo sugerencias, la lista va vacia y no es un error', () => {
    const error = new HttpErrorResponse({ status: 404, error: { codigo: 'PALABRA_NO_ENCONTRADA', mensaje: 'No está' } });

    expect(aRespuestaDeError('xyzqwk', error)).toEqual({ tipo: 'palabra-no-encontrada', palabra: 'xyzqwk', sugerencias: [] });
  });

  it('no confunde el 404 de idioma inexistente con una palabra no encontrada', () => {
    const error = new HttpErrorResponse({ status: 404, error: { codigo: 'IDIOMA_NO_DISPONIBLE', mensaje: 'No hay' } });

    expect(aRespuestaDeError('casa', error).tipo).toBe('error-al-obtener-palabra');
  });

  it('informa de que no hay servidor cuando la peticion ni sale', () => {
    const respuesta = aRespuestaDeError('casa', new HttpErrorResponse({ status: 0, error: null }));

    expect(respuesta.tipo).toBe('error-al-obtener-palabra');
    if (respuesta.tipo !== 'error-al-obtener-palabra') return;
    expect(respuesta.mensaje).toContain('No se ha podido contactar');
  });

  it('conserva los ejemplos de cada significado', () => {
    const respuesta = aPalabraEncontrada({
      idioma: 'ES',
      palabra: 'atún',
      significados: [{ texto: 'Pez de gran tamaño.', ejemplos: ['Una lata de atún'] }],
    });

    expect(respuesta).toEqual({
      tipo: 'palabra-encontrada',
      palabra: 'atún',
      significados: [{ texto: 'Pez de gran tamaño.', ejemplos: ['Una lata de atún'] }],
    });
  });
});

describe('Mapeador de idiomas', () => {
  it('traduce los codigos del servidor (ES) a los del frontal (es) y viceversa', () => {
    expect(aIdiomas(['ES', 'EN'])).toEqual([{ codigo: 'es' }, { codigo: 'en' }]);
    expect(codigoDelServidor('es')).toBe('ES');
  });
});
