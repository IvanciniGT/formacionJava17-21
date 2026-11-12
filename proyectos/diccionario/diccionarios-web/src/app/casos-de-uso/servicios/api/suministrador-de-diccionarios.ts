import type { Observable } from 'rxjs';

import type { RespuestaIdiomas } from '../../../entidades/respuesta-idiomas';
import type { RespuestaPalabra } from '../../../entidades/respuesta-palabra';

/**
 * PUERTO. El equivalente a la interfaz `SuministradorDeDiccionarios` del backend.
 *
 * Se declara como clase abstracta y no como interfaz porque en Angular una clase abstracta
 * sirve a la vez de tipo y de token de inyeccion: un concepto en lugar de dos.
 *
 * Vive en casos-de-uso y no en adaptadores porque EL PUERTO PERTENECE A QUIEN LO CONSUME.
 * La implementacion vive fuera. Si estuviera aqui, esta capa dependeria de HttpClient y la
 * arquitectura seria decorativa.
 *
 * Los Observables que devuelve NUNCA fallan: los fallos viajan como variantes de la union.
 */
export abstract class SuministradorDeDiccionarios {
  abstract getIdiomasDisponibles(): Observable<RespuestaIdiomas>;
  abstract dameSignificados(idioma: string, palabra: string): Observable<RespuestaPalabra>;
}
