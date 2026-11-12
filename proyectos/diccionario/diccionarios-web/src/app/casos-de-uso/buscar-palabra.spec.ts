import { TestBed } from '@angular/core/testing';
import { of, type Observable } from 'rxjs';

import type { RespuestaIdiomas } from '../entidades/respuesta-idiomas';
import {
  palabraEncontrada,
  palabraNoEncontrada,
  type RespuestaPalabra,
} from '../entidades/respuesta-palabra';
import { BuscarPalabra } from './buscar-palabra';
import { SuministradorDeDiccionarios } from './servicios/api/suministrador-de-diccionarios';

/**
 * Doble del puerto. Como el caso de uso depende de la ABSTRACCION y no de HttpClient, para
 * probarlo basta con inyectar esto: NO HACE FALTA MOCKEAR HTTP, ni HttpTestingController, ni
 * saber que rutas existen.
 *
 * Ese es el premio de la arquitectura, y por eso esta prueba esta aqui.
 */
class SuministradorDeDiccionariosFalso extends SuministradorDeDiccionarios {
  constructor(private readonly respuesta: RespuestaPalabra) {
    super();
  }
  getIdiomasDisponibles(): Observable<RespuestaIdiomas> {
    throw new Error('No lo usa esta prueba');
  }
  dameSignificados(): Observable<RespuestaPalabra> {
    return of(this.respuesta);
  }
}

function casoDeUsoCon(respuesta: RespuestaPalabra): BuscarPalabra {
  TestBed.configureTestingModule({
    providers: [
      { provide: SuministradorDeDiccionarios, useValue: new SuministradorDeDiccionariosFalso(respuesta) },
    ],
  });
  return TestBed.inject(BuscarPalabra);
}

describe('Caso de uso: buscar palabra', () => {
  it('empieza sin haber buscado nada', () => {
    // Dado un caso de uso recien creado
    const caso = casoDeUsoCon(palabraEncontrada('casa', []));

    // Entonces su estado inicial es "sin-buscar"
    expect(caso.estado().tipo).toBe('sin-buscar');
  });

  it('deja el resultado disponible cuando la palabra existe', () => {
    // Dado un suministrador que encuentra la palabra
    const caso = casoDeUsoCon(palabraEncontrada('casa', [{ texto: 'Edificio para habitar.', ejemplos: [] }]));

    // Cuando se busca
    caso.buscar('es', 'casa');

    // Entonces el estado es "resuelta" y trae los significados
    const estado = caso.estado();
    expect(estado.tipo).toBe('resuelta');
    if (estado.tipo !== 'resuelta') return;
    expect(estado.respuesta.tipo).toBe('palabra-encontrada');
    if (estado.respuesta.tipo !== 'palabra-encontrada') return;
    expect(estado.respuesta.significados).toEqual([{ texto: 'Edificio para habitar.', ejemplos: [] }]);
  });

  it('propaga las sugerencias cuando la palabra no existe', () => {
    // Dado un suministrador que no encuentra la palabra pero propone parecidas
    const caso = casoDeUsoCon(palabraNoEncontrada('manana', ['mañana']));

    // Cuando se busca
    caso.buscar('es', 'manana');

    // Entonces llegan las sugerencias al estado, sin haber tocado HTTP en ningun momento
    const estado = caso.estado();
    expect(estado.tipo).toBe('resuelta');
    if (estado.tipo !== 'resuelta') return;
    expect(estado.respuesta.tipo).toBe('palabra-no-encontrada');
    if (estado.respuesta.tipo !== 'palabra-no-encontrada') return;
    expect(estado.respuesta.sugerencias).toEqual(['mañana']);
  });
});
