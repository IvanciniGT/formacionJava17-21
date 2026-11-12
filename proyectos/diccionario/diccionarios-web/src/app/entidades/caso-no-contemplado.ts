/**
 * El equivalente en TypeScript a las interfaces `sealed` del backend.
 *
 * Se llama en el `default` de todo `switch` sobre una union discriminada. Como el parametro
 * es de tipo `never`, si algun dia se anade una variante y algun switch no la trata,
 * ESE SWITCH DEJA DE COMPILAR. Es justo lo que consigue `sealed` en Java.
 *
 * La excepcion en tiempo de ejecucion no deberia saltar nunca: es la red de seguridad por si
 * llega un valor que el compilador no pudo comprobar.
 */
export function casoNoContemplado(valor: never): never {
  throw new Error(`Caso no contemplado: ${JSON.stringify(valor)}`);
}
