import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter, map } from 'rxjs';

import { ListarIdiomasDisponibles } from '../../../casos-de-uso/listar-idiomas-disponibles';
import { CampoDePalabra } from './campo-de-palabra/campo-de-palabra';
import { SelectorDeIdioma } from './selector-de-idioma/selector-de-idioma';

/**
 * Orquesta la busqueda y nada mas: no pinta controles, los compone.
 *
 * BUSCAR ES NAVEGAR. Este componente no guarda el resultado ni llama al servicio de
 * busqueda: cambia la URL. La URL es la fuente de la verdad, asi que atras, adelante,
 * recargar y compartir un enlace funcionan sin escribir una linea para ello.
 */
@Component({
  selector: 'app-buscador',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SelectorDeIdioma, CampoDePalabra],
  templateUrl: './buscador.html',
  styleUrl: './buscador.css',
})
export class Buscador {
  private readonly router = inject(Router);
  private readonly listado = inject(ListarIdiomasDisponibles);

  readonly idiomas = this.listado.idiomas;

  /**
   * Idioma y palabra se leen de la URL: no hay estado duplicado que pueda discrepar.
   *
   * `router.url` es una propiedad normal, no una signal, asi que en zoneless no bastaria:
   * nadie avisaria de que ha cambiado. Se convierte en signal escuchando NavigationEnd.
   */
  private readonly url = toSignal(
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map(e => e.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );

  private readonly segmentos = computed(() =>
    this.url().split('?')[0].split('/').filter(Boolean).map(decodeURIComponent),
  );

  protected readonly idioma = computed(
    () => this.segmentos()[0] ?? this.idiomaPorDefecto(),
  );

  /**
   * Si la URL no dice nada, se propone el castellano. Coger `idiomas()[0]` seria coger el
   * primero por orden alfabetico, que resulta ser el ingles: arbitrario y desconcertante en
   * una aplicacion en castellano. Si algun dia no hubiera espanol, se cae al primero que haya.
   */
  private idiomaPorDefecto(): string | null {
    const disponibles = this.idiomas();
    return disponibles.find(i => i.codigo === 'es')?.codigo ?? disponibles[0]?.codigo ?? null;
  }
  protected readonly palabra = computed(() => this.segmentos()[1] ?? '');

  constructor() {
    this.listado.cargar();
  }

  protected cambiarIdioma(codigo: string): void {
    const palabra = this.palabra();
    if (palabra) this.router.navigate([codigo, palabra]);
  }

  protected irABusqueda(palabra: string): void {
    const idioma = this.idioma();
    if (idioma) this.router.navigate([idioma, palabra]);
  }
}
