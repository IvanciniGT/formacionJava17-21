// Java 9: módulo de la plataforma (JPMS): qué necesita (requires), qué deja ver (exports) y qué servicios usa u ofrece (uses / provides).
module impl.service.diccionarios.rest {
    requires api.service.diccionarios;
    requires static lombok;
    // Automatic-Module-Name del jar del API REST: no tiene module-info.
    requires api.controlador.rest;
    // HttpClient no está en java.base.
    requires java.net.http;
    requires com.google.gson;

    // No exporta nada: nadie salvo el ServiceLoader puede crear instancias de sus clases.
    provides com.curso.diccionario.service.api.BuscadorDePalabras
            with com.curso.diccionario.service.rest.BuscadorDePalabrasRemoto;
}
