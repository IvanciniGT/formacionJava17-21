import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * Presenta la variante `palabra-no-encontrada` CUANDO hay parecidas.
 *
 * Cada sugerencia es un RouterLink: pinchar es navegar, y como la URL es la fuente de la
 * verdad, el resultado se repinta solo y el boton de atras funciona.
 */
@Component({
  selector: 'app-lista-de-sugerencias',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  templateUrl: './lista-de-sugerencias.html',
  styleUrl: './lista-de-sugerencias.css',
})
export class ListaDeSugerencias {
  readonly palabra = input.required<string>();
  readonly idioma = input.required<string>();
  readonly sugerencias = input.required<readonly string[]>();
}
