import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { catchError, map, of, type Observable } from 'rxjs';

import { SuministradorDeDiccionarios } from '../../../casos-de-uso/servicios/api/suministrador-de-diccionarios';
import {
  errorAlObtenerIdiomas,
  idiomasDisponibles,
  type RespuestaIdiomas,
} from '../../../entidades/respuesta-idiomas';
import type { RespuestaPalabra } from '../../../entidades/respuesta-palabra';
import type { CodigosDeIdiomaDto, PalabraDto } from '../../dto/dtos';
import { aIdiomas, codigoDelServidor } from '../../mapeadores/idioma.mapeador';
import { aPalabraEncontrada, aRespuestaDeError, mensajeDeRed } from '../../mapeadores/respuesta-palabra.mapeador';

/** La misma raiz que ApiRestDiccionarios.RUTA_DICCIONARIOS del backend. */
const RAIZ = '/api/v1/diccionarios';

/**
 * ADAPTADOR DE SALIDA. Implementa el puerto hablando HTTP con el servidor central de
 * diccionarios. Es el unico sitio de toda la aplicacion que conoce HttpClient, las URLs y los DTOs.
 *
 * REGLA QUE NO SE ROMPE: por el canal de error del Observable no pasa NADA. `catchError` lo
 * convierte en un valor de la union, asi que estos Observables no fallan: fallan DENTRO DEL TIPO.
 */
@Injectable()
export class SuministradorDeDiccionariosRest extends SuministradorDeDiccionarios {
  private readonly http = inject(HttpClient);

  getIdiomasDisponibles(): Observable<RespuestaIdiomas> {
    return this.http.get<CodigosDeIdiomaDto>(RAIZ).pipe(
      map(dtos => idiomasDisponibles(aIdiomas(dtos))),
      catchError((e: HttpErrorResponse) => of(errorAlObtenerIdiomas(mensajeDeRed(e)))),
    );
  }

  /**
   * Una sola peticion. Si la palabra no existe, el servidor responde 404 PALABRA_NO_ENCONTRADA y
   * manda las palabras parecidas EN EL CUERPO de ese 404: el mapeador las saca de ahi.
   */
  dameSignificados(idioma: string, palabra: string): Observable<RespuestaPalabra> {
    const url = `${RAIZ}/${encodeURIComponent(codigoDelServidor(idioma))}/palabras/${encodeURIComponent(palabra)}`;
    // De las dos búsquedas del servicio, la que encuentra "atún" aunque se escriba "Atun": es lo
    // que espera quien teclea en un buscador.
    return this.http.get<PalabraDto>(url, { params: { ignorarMayusculasYAcentos: true } }).pipe(
      map(aPalabraEncontrada),
      catchError((e: HttpErrorResponse) => of(aRespuestaDeError(palabra, e))),
    );
  }
}
