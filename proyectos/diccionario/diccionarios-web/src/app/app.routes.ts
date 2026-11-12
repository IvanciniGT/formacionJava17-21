import type { Routes } from '@angular/router';

import { Bienvenida } from './infraestructura/ui/bienvenida/bienvenida';
import { Resultado } from './infraestructura/ui/resultado/resultado';

/**
 * La URL es la fuente de la verdad de la busqueda.
 *
 * Con withComponentInputBinding(), :idioma y :palabra llegan a Resultado como input() de
 * signal. Buscar es navegar.
 */
export const routes: Routes = [
  { path: '', component: Bienvenida, title: 'Diccionarios' },
  { path: ':idioma/:palabra', component: Resultado },
  { path: '**', redirectTo: '' },
];
