import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import type { Significado } from '../../../../entidades/respuesta-palabra';

/** Presenta la variante `palabra-encontrada`. */
@Component({
  selector: 'app-lista-de-significados',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './lista-de-significados.html',
  styleUrl: './lista-de-significados.css',
})
export class ListaDeSignificados {
  readonly significados = input.required<readonly Significado[]>();
}
