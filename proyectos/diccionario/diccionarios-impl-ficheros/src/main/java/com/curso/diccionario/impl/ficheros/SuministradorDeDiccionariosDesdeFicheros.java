package com.curso.diccionario.impl.ficheros;

import java.io.IOException;
import java.lang.System.Logger.Level;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import com.curso.diccionario.api.Diccionario;
import com.curso.diccionario.api.SuministradorDeDiccionarios;
import com.curso.diccionario.impl.ficheros.formato.CatalogoDeDiccionariosEnClasspath;
import com.curso.diccionario.impl.ficheros.formato.FormatoDeFicheroDeDiccionario;

import lombok.NonNull;


// La lectura de los archivos la hacemos en modo LAZY (cuando se pida la primera vez un diccionario de un idioma se lee el archivo)
// Y se cachea en memoria.
// Siguientes peticiones hacen uso de cache!
// Para la cache, que estructura de datos podemos usar?
public class SuministradorDeDiccionariosDesdeFicheros implements SuministradorDeDiccionarios {

    static final String VARIABLE_DE_ENTORNO_CARPETA = "DICCIONARIOS_CARPETA";
    // Sin la variable, los diccionarios se buscan dentro de los jars del classpath, no en disco:
    // los de los proyectos diccionario-es, diccionario-en...
    static final String CARPETA_EN_CLASSPATH_POR_DEFECTO = CatalogoDeDiccionariosEnClasspath.CARPETA_POR_DEFECTO;
    private static final String EXTENSION = ".txt";

    // System.Logger: viene en el JDK (java.base), así que no añade ninguna dependencia.
    private static final System.Logger log = System.getLogger(SuministradorDeDiccionariosDesdeFicheros.class.getName());

    private final String carpetaConFicherosDeDiccionarios;
    private final CatalogoDeDiccionariosEnClasspath catalogo;

    private final Map<String, Diccionario> cacheDeDiccionarios = new WeakHashMap<>();
    // Un Map no es una buena elección para una cache.
    // El problema es que va engordando indefinidamente y nunca se eliminan entradas antiguas.
    // Puede acabar el sistema colapsando.
    // Una cache deberia poder eliminar datos si es necesario.
    // Hay una implementaciónd de Map que en caso de ser necesario elimina automáticamente las entradas.
    // Lo hace a petición del garbage collector. Si el garbage collector necesita memoria.
    // Podríamos usar otro tipo de map, con politicas de evition (como LRU - Least Recently Used, como los más antiguos se eliminan primero).
    // Para no complicarnos vamos a usar un WeakHashMap, que elimina automáticamente las entradas cuando las claves dejan de ser referenciadas.
    // WeakHashMap... lo que guarda son referencias débiles a las claves. Si un dato deja de ser referenciado en otro lugar, la entrada se elimina automáticamente cuando el garbage collector lo considere necesario.
    // Me quito del problema de gestionar la cache... Puede engordar un huevo... pero si es necesario, se eliminarán automáticamente las entradas viejas.
    // En java exste el concepto de WeakRefecence, que me permite crear variables que no impiden que el garbage collector elimine el objeto al que apuntan si no hay otras referencias fuertes.

    public SuministradorDeDiccionariosDesdeFicheros(String carpetaConFicherosDeDiccionarios) {
        this.carpetaConFicherosDeDiccionarios = carpetaConFicherosDeDiccionarios;
        this.catalogo = new CatalogoDeDiccionariosEnClasspath(getClass().getClassLoader(), carpetaConFicherosDeDiccionarios);
    }

    // El ServiceLoader sabe instanciar de dos formas: en el module path, con este método provider()
    // (por el provides de module-info); en el classpath (el servidor con Spring), con un
    // constructor público sin argumentos y el fichero META-INF/services. Están las dos.
    public static SuministradorDeDiccionarios provider() {
        return new SuministradorDeDiccionariosDesdeFicheros();
    }

    public SuministradorDeDiccionariosDesdeFicheros() {
        this(carpetaConfigurada());
    }

    private static String carpetaConfigurada() {
        String carpeta = System.getenv(VARIABLE_DE_ENTORNO_CARPETA);
        return carpeta != null ? carpeta : CARPETA_EN_CLASSPATH_POR_DEFECTO;
    }

    // getIdiomas no puede lanzar nada: el API no lo permite. Si no se puede leer la carpeta,
    // solo queda avisar en el log y devolver una lista vacía... que el que pregunta no sabrá
    // distinguir de "no hay idiomas". (Lo arreglará la 1.2.0 del API.)
    @Override
    public List<String> getIdiomas() {
        try {
            return idiomasEnLaCarpeta();
        } catch (IOException e) {
            log.log(Level.WARNING, "No se pudieron averiguar los idiomas disponibles", e);
            // Java 9: List.of / Map.of / Set.of, colecciones inmutables en una línea.
            return List.of();
        }
    }

    private List<String> idiomasEnLaCarpeta() throws IOException {
        // Java 11: Path.of y Files.readString / writeString.
        Path carpeta = Path.of(carpetaConFicherosDeDiccionarios);
        if (!Files.isDirectory(carpeta)) {
            return catalogo.idiomas();
        }
        // Java 8: streams, el map-reduce de Java (stream, map, filter, collect...).
        try (Stream<Path> ficheros = Files.list(carpeta)) {
            // Java 8: lambda, una función anónima escrita dentro de una expresión.
            return ficheros.map(fichero -> fichero.getFileName().toString())
                    // Un solo punto: es.prueba.txt no es el diccionario de un idioma "ES.PRUEBA".
                    .filter(nombre -> nombre.endsWith(EXTENSION) && nombre.indexOf('.') == nombre.length() - EXTENSION.length())
                    .map(nombre -> nombre.substring(0, nombre.length() - EXTENSION.length()).toUpperCase(Locale.ROOT))
                    // Java 16: Stream.toList(), lista inmutable sin Collectors.toList().
                    .toList();
        }
    }

    @Override
    public boolean tienesDiccionarioDe(@NonNull String idioma) {
        return cacheDeDiccionarios.containsKey(idioma) || getFicheroParaIdioma(idioma).isPresent();
    }

    // Java 8: Optional, para decir en la firma que el valor puede no estar, en vez de devolver null.
    @Override
    public Optional<Diccionario> getDiccionario(@NonNull String idioma) {
        // Siempre devuelvo desde cache
        Diccionario enCache = cacheDeDiccionarios.get(idioma);
        if (enCache != null) {
            return Optional.of(enCache);
        }
        Optional<URL> fichero = getFicheroParaIdioma(idioma);
        if (fichero.isEmpty()) {
            return Optional.empty();
        }
        try {
            Diccionario leido = new DiccionarioDesdeFichero(idioma, FormatoDeFicheroDeDiccionario.leer(fichero.get()));
            cacheDeDiccionarios.put(idioma, leido);
            return Optional.of(leido);
        } catch (IOException e) {
            // El API no deja avisar de un error: un fichero ilegible acaba pareciendo
            // "no tengo ese idioma". Es lo que obliga a cambiar el API en la 1.1.0.
            log.log(Level.WARNING, "No se pudo cargar el diccionario de " + idioma, e);
            return Optional.empty();
        }
    }

    // URL y no Path: un fichero dentro de un jar no es un Path del disco, pero sí tiene URL.
    private Optional<URL> getFicheroParaIdioma(String idioma) {
        Path carpeta = Path.of(carpetaConFicherosDeDiccionarios);
        if (!Files.isDirectory(carpeta)) {
            return catalogo.fichero(idioma);
        }
        // Locale.ROOT: con el locale turco, "I".toLowerCase() no da "i".
        Path fichero = carpeta.resolve(idioma.toLowerCase(Locale.ROOT) + EXTENSION);
        if (!Files.isRegularFile(fichero)) {
            return Optional.empty();
        }
        try {
            return Optional.of(fichero.toUri().toURL());
        } catch (MalformedURLException e) {
            log.log(Level.WARNING, "Ruta de diccionario no válida " + fichero, e);
            return Optional.empty();
        }
    }

}
