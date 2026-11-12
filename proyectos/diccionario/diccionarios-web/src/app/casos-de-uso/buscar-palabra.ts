import { Injectable, inject, signal } from '@angular/core';

import type { RespuestaPalabra } from '../entidades/respuesta-palabra';
import { SuministradorDeDiccionarios } from './servicios/api/suministrador-de-diccionarios';

/**
 * Estado de una busqueda.
 *
 * Es otra union discriminada: en vez de tener un booleano `cargando` mas un resultado que
 * puede ser nulo -combinacion que permite estados imposibles como "cargando y con error"-,
 * cada situacion es una forma distinta y solo una puede darse a la vez.
 */
export type EstadoDeBusqueda =
  | { readonly tipo: 'sin-buscar' }
  | { readonly tipo: 'buscando'; readonly palabra: string }
  | { readonly tipo: 'resuelta'; readonly respuesta: RespuestaPalabra };

/** CASO DE USO: buscar los significados de una palabra. Solo conoce el puerto. */
@Injectable({ providedIn: 'root' })
export class BuscarPalabra {
  private readonly suministrador = inject(SuministradorDeDiccionarios);

  private readonly _estado = signal<EstadoDeBusqueda>({ tipo: 'sin-buscar' });
  readonly estado = this._estado.asReadonly();

  buscar(idioma: string, palabra: string): void {
    this._estado.set({ tipo: 'buscando', palabra });
    // El Observable no falla nunca: el error viaja como variante de RespuestaPalabra.
    this.suministrador
      .dameSignificados(idioma, palabra)
      .subscribe(respuesta => this._estado.set({ tipo: 'resuelta', respuesta }));
  }
}
