import { ChangeDetectionStrategy, Component } from '@angular/core';

/** Ruta '/': todavia no se ha buscado nada. */
@Component({
  selector: 'app-bienvenida',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './bienvenida.html',
  styleUrl: './bienvenida.css',
})
export class Bienvenida {}
