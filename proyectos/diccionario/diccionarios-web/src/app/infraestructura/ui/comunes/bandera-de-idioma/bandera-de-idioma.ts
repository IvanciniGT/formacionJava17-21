import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { banderaDe, nombreDe } from '../../../idiomas/catalogo-de-idiomas';

/**
 * Bandera de un idioma.
 *
 * PNG y no emoji: Windows no trae los glifos de bandera y pintaria "FR" en lugar de 🇫🇷.
 *
 * Accesibilidad: `alt` lleva el nombre del idioma solo cuando la bandera va SOLA. Si al lado
 * ya se lee el nombre, se pasa `conTexto` y el alt queda vacio para que el lector de pantalla
 * no lo diga dos veces. El significado nunca depende del icono.
 */
@Component({
  selector: 'app-bandera-de-idioma',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './bandera-de-idioma.html',
  styleUrl: './bandera-de-idioma.css',
})
export class BanderaDeIdioma {
  readonly codigo = input.required<string>();
  /** true cuando el nombre del idioma ya se lee al lado. */
  readonly conTexto = input(false);
  readonly tam = input(24);

  protected readonly src = computed(() => banderaDe(this.codigo()));
  protected readonly alt = computed(() => (this.conTexto() ? '' : nombreDe(this.codigo())));
}
