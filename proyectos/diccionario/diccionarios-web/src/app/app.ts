import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { ServicioDeTema } from './infraestructura/tema/servicio-de-tema';
import { Layout } from './infraestructura/ui/layout/layout';

@Component({
  selector: 'app-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [Layout],
  templateUrl: './app.html',
})
export class App {
  // Instanciar el servicio aplica el tema guardado antes del primer pintado.
  private readonly tema = inject(ServicioDeTema);
}
