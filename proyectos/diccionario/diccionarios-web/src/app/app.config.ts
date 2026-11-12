import { provideHttpClient, withFetch } from '@angular/common/http';
import {
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
  type ApplicationConfig,
} from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';

import { routes } from './app.routes';
import { SuministradorDeDiccionariosRest } from './adaptadores/servicios/impl/suministrador-de-diccionarios-rest';
import { SuministradorDeDiccionarios } from './casos-de-uso/servicios/api/suministrador-de-diccionarios';

/**
 * RAIZ DE COMPOSICION.
 *
 * El unico fichero de toda la aplicacion que conoce una clase concreta. Es, literalmente, el
 * equivalente a SuministradorDeDiccionariosFactory.getInstance() del backend: cambiar de
 * implementacion es cambiar una linea.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withFetch()),

    { provide: SuministradorDeDiccionarios, useClass: SuministradorDeDiccionariosRest },
  ],
};
