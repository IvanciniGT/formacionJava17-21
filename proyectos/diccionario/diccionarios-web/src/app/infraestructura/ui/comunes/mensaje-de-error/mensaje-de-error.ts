import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

/** Presenta la variante de error de cualquiera de las uniones del dominio. */
@Component({
  selector: 'app-mensaje-de-error',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatIconModule],
  templateUrl: './mensaje-de-error.html',
  styleUrl: './mensaje-de-error.css',
})
export class MensajeDeError {
  readonly titulo = input('Algo no ha ido bien');
  readonly mensaje = input.required<string>();
}
