/**
 * PRESENTACION, no dominio. Por eso vive en infraestructura y no en entidades.
 *
 * El backend solo dice que existe el diccionario "es". Como se llama eso de cara a una
 * persona, y con que bandera se ilustra, es decision del frontal.
 *
 * COSTE ASUMIDO: cuando el backend publique un diccionario nuevo, aparecera igual en el
 * selector -la lista viene del servidor-, pero sin nombre bonito hasta que se anada aqui.
 * Por eso `nombreDe` y `banderaDe` degradan con elegancia en vez de romperse.
 */
interface EntradaDeCatalogo {
  readonly nombre: string;
  readonly bandera: string;
}

const CATALOGO: Readonly<Record<string, EntradaDeCatalogo>> = {
  es: { nombre: 'Español', bandera: 'banderas/es.png' },
  en: { nombre: 'Inglés', bandera: 'banderas/en.png' },
  fr: { nombre: 'Francés', bandera: 'banderas/fr.png' },
  it: { nombre: 'Italiano', bandera: 'banderas/it.png' },
};

/** Nombre para mostrar. Si el idioma no esta en el catalogo, devuelve el codigo en mayusculas. */
export function nombreDe(codigo: string): string {
  return CATALOGO[codigo]?.nombre ?? codigo.toUpperCase();
}

/** Ruta de la bandera, o null si no hay ninguna. Quien la use debe aguantar el null. */
export function banderaDe(codigo: string): string | null {
  return CATALOGO[codigo]?.bandera ?? null;
}
