package com.curso.diccionario.cargador;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.transaction.support.TransactionTemplate;

import com.curso.diccionario.api.Significado;
import com.curso.diccionario.cargador.modelo.HuellaDeFichero;
import com.curso.diccionario.cargador.modelo.RepositorioDeHuellas;
import com.curso.diccionario.impl.bbdd.AlmacenDeDiccionarios;
import com.curso.diccionario.impl.ficheros.formato.FormatoDeFicheroDeDiccionario;

// Al arrancar el servidor, deja la BBDD igual que los ficheros de los jars de idioma, pero sin
// recargar lo que no ha cambiado. Se compara la huella (SHA-256) del fichero, no el número de
// palabras: arreglar una errata (diccionario-en 1.0.1) no cambia cuántas hay, y también tiene
// que llegar a la BBDD.
//  - fichero con la misma huella que la guardada: no se toca;
//  - huella distinta o idioma nuevo: se borran sus palabras y se cargan las del fichero;
//  - idioma en BBDD cuyo jar ya no está: se borra. Los idiomas disponibles son los jars que hay.
public class CargadorDeDiccionarios implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CargadorDeDiccionarios.class);

    private final FuenteDeDiccionarios fuente;
    private final AlmacenDeDiccionarios almacen;
    private final RepositorioDeHuellas huellas;
    private final TransactionTemplate transacciones;

    public CargadorDeDiccionarios(FuenteDeDiccionarios fuente, AlmacenDeDiccionarios almacen,
            RepositorioDeHuellas huellas, TransactionTemplate transacciones) {
        this.fuente = fuente;
        this.almacen = almacen;
        this.huellas = huellas;
        this.transacciones = transacciones;
    }

    @Override
    public void run(ApplicationArguments argumentos) throws IOException {
        cargar();
    }

    // Devuelve los idiomas que ha tenido que (re)cargar: así las pruebas ven si la huella evitó la recarga.
    public List<String> cargar() throws IOException {
        List<String> enLaFuente = fuente.idiomas();
        List<String> recargados = new ArrayList<>();
        for (String idioma : enLaFuente) {
            byte[] contenido = fuente.contenido(idioma);
            String huella = sha256(contenido);
            // Java 8: referencia a método (::), una lambda que solo llama a un método.
            if (huellas.findById(idioma).map(HuellaDeFichero::getSha256).filter(huella::equals).isPresent()) {
                log.info("Diccionario {} sin cambios (huella {}): no se recarga", idioma, huella);
                continue;
            }
            Map<String, List<Significado>> palabras = FormatoDeFicheroDeDiccionario.leer(contenido);
            // Las palabras y la huella, en la misma transacción: si la carga falla a medias, no
            // queda apuntada una huella de algo que no está en la BBDD.
            // Java 8: lambda, una función anónima escrita dentro de una expresión.
            transacciones.executeWithoutResult(estado -> {
                almacen.recargar(idioma, palabras);
                huellas.save(new HuellaDeFichero(idioma, huella, Instant.now()));
            });
            recargados.add(idioma);
            log.info("Diccionario {} cargado en BBDD (huella {})", idioma, huella);
        }
        for (String idioma : almacen.idiomas()) {
            if (!enLaFuente.contains(idioma)) {
                transacciones.executeWithoutResult(estado -> {
                    almacen.borrar(idioma);
                    huellas.deleteById(idioma);
                });
                log.info("Diccionario {} borrado de BBDD: ya no hay fichero", idioma);
            }
        }
        return recargados;
    }

    static String sha256(byte[] contenido) {
        try {
            // Java 17: HexFormat, bytes a hexadecimal sin bucles a mano.
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenido));
        } catch (NoSuchAlgorithmException e) {
            // Todo JDK está obligado a traer SHA-256: si falta, el entorno está roto.
            throw new IllegalStateException(e);
        }
    }
}
