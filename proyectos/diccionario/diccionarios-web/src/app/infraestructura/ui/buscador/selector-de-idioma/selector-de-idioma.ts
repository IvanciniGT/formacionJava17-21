import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';

import type { Idioma } from '../../../../entidades/idioma';
import { nombreDe } from '../../../idiomas/catalogo-de-idiomas';
import { BanderaDeIdioma } from '../../comunes/bandera-de-idioma/bandera-de-idioma';

/** Solo elige idioma. Ni busca, ni sabe de HTTP. */
@Component({
  selector: 'app-selector-de-idioma',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatSelectModule, BanderaDeIdioma],
  templateUrl: './selector-de-idioma.html',
  styleUrl: './selector-de-idioma.css',
})
export class SelectorDeIdioma {
  readonly idiomas = input.required<readonly Idioma[]>();
  readonly seleccionado = input<string | null>(null);
  readonly cambiado = output<string>();

  protected nombre = nombreDe;
}
