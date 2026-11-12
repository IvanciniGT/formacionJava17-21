import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { Buscador } from '../buscador/buscador';
import { Cabecera } from './cabecera/cabecera';

/**
 * El buscador vive AQUI y no dentro de cada ruta.
 *
 * Si estuviera en la pagina habria que duplicarlo, o remontarlo en cada navegacion perdiendo
 * lo escrito. Desde el layout, la URL manda sobre el resultado y el formulario sobrevive:
 * pinchas una sugerencia, cambia la ruta, se repinta el resultado, y el buscador sigue ahi.
 */
@Component({
  selector: 'app-layout',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, Cabecera, Buscador],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {}
