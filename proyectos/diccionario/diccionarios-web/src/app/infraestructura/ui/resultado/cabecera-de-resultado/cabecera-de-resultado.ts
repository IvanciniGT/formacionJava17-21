import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { nombreDe } from '../../../idiomas/catalogo-de-idiomas';
import { BanderaDeIdioma } from '../../comunes/bandera-de-idioma/bandera-de-idioma';

@Component({
  selector: 'app-cabecera-de-resultado',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [BanderaDeIdioma],
  templateUrl: './cabecera-de-resultado.html',
  styleUrl: './cabecera-de-resultado.css',
})
export class CabeceraDeResultado {
  readonly palabra = input.required<string>();
  readonly idioma = input.required<string>();
  protected readonly nombre = computed(() => nombreDe(this.idioma()));
}
