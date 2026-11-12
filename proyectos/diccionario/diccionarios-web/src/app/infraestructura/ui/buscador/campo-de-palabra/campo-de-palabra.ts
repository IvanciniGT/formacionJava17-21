import { ChangeDetectionStrategy, Component, input, linkedSignal, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

/** Recoge la palabra y avisa. No decide nada mas. */
@Component({
  selector: 'app-campo-de-palabra',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule],
  templateUrl: './campo-de-palabra.html',
  styleUrl: './campo-de-palabra.css',
})
export class CampoDePalabra {
  readonly inicial = input('');
  readonly deshabilitado = input(false);
  readonly buscar = output<string>();

  /**
   * linkedSignal: arranca con lo que venga de la ruta, pero deja escribir encima.
   * Si la URL cambia (al pinchar una sugerencia), el campo se resincroniza solo.
   */
  protected readonly texto = linkedSignal(() => this.inicial());

  protected enviar(): void {
    const palabra = this.texto().trim();
    if (palabra) this.buscar.emit(palabra);
  }
}
