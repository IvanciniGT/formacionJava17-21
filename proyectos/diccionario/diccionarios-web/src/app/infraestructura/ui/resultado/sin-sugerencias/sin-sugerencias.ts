import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

/**
 * Presenta la variante `palabra-no-encontrada` cuando NO hay ninguna parecida.
 *
 * Componente aparte, pero la misma variante de la union: el backend distingue
 * `sugerencias: []` de que el campo no venga, y esa diferencia merece verse en pantalla.
 * No se anadio una variante mas al tipo para no acabar con cuarenta clases de respuesta.
 */
@Component({
  selector: 'app-sin-sugerencias',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatIconModule],
  templateUrl: './sin-sugerencias.html',
  styleUrl: './sin-sugerencias.css',
})
export class SinSugerencias {
  readonly palabra = input.required<string>();
}
