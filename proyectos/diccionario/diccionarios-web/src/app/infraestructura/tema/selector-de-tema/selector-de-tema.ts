import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatIconModule } from '@angular/material/icon';

import { ServicioDeTema, type Tema } from '../servicio-de-tema';

/** Tres estados, no un interruptor: claro, oscuro y el del sistema. */
@Component({
  selector: 'app-selector-de-tema',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonToggleModule, MatIconModule],
  templateUrl: './selector-de-tema.html',
  styleUrl: './selector-de-tema.css',
})
export class SelectorDeTema {
  private readonly servicio = inject(ServicioDeTema);
  protected readonly tema = this.servicio.tema;

  protected cambiar(tema: Tema): void {
    if (tema) this.servicio.cambiar(tema);
  }
}
