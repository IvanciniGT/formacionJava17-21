import { Injectable, effect, signal } from '@angular/core';

/** Tres estados, no un interruptor de dos: el del sistema es una eleccion en si misma. */
export type Tema = 'claro' | 'oscuro' | 'sistema';

const CLAVE = 'diccionarios-web.tema';

@Injectable({ providedIn: 'root' })
export class ServicioDeTema {
  private readonly _tema = signal<Tema>(this.leerPreferencia());
  readonly tema = this._tema.asReadonly();

  constructor() {
    // Repinta el atributo del <html> cada vez que cambia el tema.
    effect(() => this.aplicar(this._tema()));

    // Si el usuario esta en "sistema", hay que reaccionar cuando el sistema cambie.
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
      if (this._tema() === 'sistema') this.aplicar('sistema');
    });
  }

  cambiar(tema: Tema): void {
    this._tema.set(tema);
    try {
      localStorage.setItem(CLAVE, tema);
    } catch {
      // Navegacion privada o almacenamiento bloqueado: la aplicacion sigue funcionando.
    }
  }

  private aplicar(tema: Tema): void {
    const oscuro =
      tema === 'oscuro' ||
      (tema === 'sistema' && window.matchMedia('(prefers-color-scheme: dark)').matches);
    document.documentElement.dataset['tema'] = oscuro ? 'oscuro' : 'claro';
  }

  private leerPreferencia(): Tema {
    try {
      const guardado = localStorage.getItem(CLAVE);
      if (guardado === 'claro' || guardado === 'oscuro' || guardado === 'sistema') return guardado;
    } catch {
      // Sin acceso al almacenamiento: se usa el del sistema, que es el valor por defecto.
    }
    return 'sistema';
  }
}
