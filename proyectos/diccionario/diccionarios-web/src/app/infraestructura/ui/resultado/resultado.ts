import { ChangeDetectionStrategy, Component, effect, inject, input } from '@angular/core';

import { BuscarPalabra } from '../../../casos-de-uso/buscar-palabra';
import { casoNoContemplado } from '../../../entidades/caso-no-contemplado';
import { Cargando } from '../comunes/cargando/cargando';
import { MensajeDeError } from '../comunes/mensaje-de-error/mensaje-de-error';
import { CabeceraDeResultado } from './cabecera-de-resultado/cabecera-de-resultado';
import { ListaDeSignificados } from './lista-de-significados/lista-de-significados';
import { ListaDeSugerencias } from './lista-de-sugerencias/lista-de-sugerencias';
import { SinSugerencias } from './sin-sugerencias/sin-sugerencias';

/**
 * Vista de la ruta /:idioma/:palabra.
 *
 * `idioma` y `palabra` llegan como input() de signal directamente desde la ruta, gracias a
 * withComponentInputBinding(). LA URL ES LA FUENTE DE LA VERDAD: este componente no guarda
 * que se busco, lo lee de donde ya esta.
 *
 * No dibuja resultados: hace el switch exhaustivo sobre la union y delega en el componente
 * de cada variante. Asi la union sellada del dominio se ve en la pantalla.
 */
@Component({
  selector: 'app-resultado',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CabeceraDeResultado,
    ListaDeSignificados,
    ListaDeSugerencias,
    SinSugerencias,
    Cargando,
    MensajeDeError,
  ],
  templateUrl: './resultado.html',
  styleUrl: './resultado.css',
})
export class Resultado {
  readonly idioma = input.required<string>();
  readonly palabra = input.required<string>();

  private readonly caso = inject(BuscarPalabra);
  protected readonly estado = this.caso.estado;

  constructor() {
    // Cambiar la URL dispara la busqueda. Buscar es navegar.
    effect(() => this.caso.buscar(this.idioma(), this.palabra()));
  }

  /** Red de seguridad del compilador: si se anade una variante, esto deja de compilar. */
  protected readonly casoNoContemplado = casoNoContemplado;
}
