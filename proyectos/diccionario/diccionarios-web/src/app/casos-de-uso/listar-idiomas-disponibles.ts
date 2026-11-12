import { Injectable, inject, signal } from '@angular/core';

import type { Idioma } from '../entidades/idioma';
import { casoNoContemplado } from '../entidades/caso-no-contemplado';
import { SuministradorDeDiccionarios } from './servicios/api/suministrador-de-diccionarios';

/**
 * CASO DE USO: saber que diccionarios ofrece el servidor.
 *
 * La lista se pide una sola vez y se comparte: el selector la necesita en cada pantalla y no
 * tiene sentido preguntar lo mismo una y otra vez.
 */
@Injectable({ providedIn: 'root' })
export class ListarIdiomasDisponibles {
  private readonly suministrador = inject(SuministradorDeDiccionarios);

  private readonly _idiomas = signal<readonly Idioma[]>([]);
  private readonly _error = signal<string | null>(null);
  private pedidos = false;

  readonly idiomas = this._idiomas.asReadonly();
  readonly error = this._error.asReadonly();

  cargar(): void {
    if (this.pedidos) return;
    this.pedidos = true;

    this.suministrador.getIdiomasDisponibles().subscribe(respuesta => {
      switch (respuesta.tipo) {
        case 'idiomas-disponibles':
          this._idiomas.set(respuesta.idiomas);
          this._error.set(null);
          break;
        case 'error-al-obtener-idiomas':
          this._error.set(respuesta.mensaje);
          break;
        default:
          casoNoContemplado(respuesta);
      }
    });
  }
}
