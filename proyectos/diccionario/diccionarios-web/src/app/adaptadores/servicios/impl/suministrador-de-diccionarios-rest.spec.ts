import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import type { RespuestaPalabra } from '../../../entidades/respuesta-palabra';
import { SuministradorDeDiccionariosRest } from './suministrador-de-diccionarios-rest';

/**
 * Lo que el mapeador no puede probar solo: la peticion que de verdad sale (con la busqueda que
 * ignora mayusculas y acentos) y que el cuerpo del 404 acaba en el dominio.
 */
describe('Suministrador REST', () => {
  let suministrador: SuministradorDeDiccionariosRest;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SuministradorDeDiccionariosRest, provideHttpClient(), provideHttpClientTesting()],
    });
    suministrador = TestBed.inject(SuministradorDeDiccionariosRest);
    http = TestBed.inject(HttpTestingController);
  });

  // Y de paso comprueba que no hay ninguna peticion de mas.
  afterEach(() => http.verify());

  function buscar(idioma: string, palabra: string): RespuestaPalabra[] {
    const recibidas: RespuestaPalabra[] = [];
    suministrador.dameSignificados(idioma, palabra).subscribe(r => recibidas.push(r));
    return recibidas;
  }

  it('pide la busqueda que ignora mayusculas y acentos, con el codigo del idioma del servidor', () => {
    const recibidas = buscar('es', 'Atun');

    http.expectOne('/api/v1/diccionarios/ES/palabras/Atun?ignorarMayusculasYAcentos=true').flush({
      idioma: 'ES',
      palabra: 'atún',
      significados: [{ texto: 'Pez.', ejemplos: [] }],
    });

    expect(recibidas[0].tipo).toBe('palabra-encontrada');
  });

  it('saca las sugerencias del cuerpo del 404, sin pedir nada mas', () => {
    const recibidas = buscar('es', 'membrilo');

    http.expectOne('/api/v1/diccionarios/ES/palabras/membrilo?ignorarMayusculasYAcentos=true').flush(
      { codigo: 'PALABRA_NO_ENCONTRADA', mensaje: 'No está', sugerencias: ['membrillo'] },
      { status: 404, statusText: 'Not Found' },
    );

    expect(recibidas).toEqual([{ tipo: 'palabra-no-encontrada', palabra: 'membrilo', sugerencias: ['membrillo'] }]);
  });
});
