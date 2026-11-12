// Java 9: módulo de la plataforma (JPMS): qué necesita (requires), qué deja ver (exports) y qué servicios usa u ofrece (uses / provides).
module api.ui.consola {
    // transitive: quien use este API ve Significado en sus firmas y lo tiene que poder nombrar.
    requires transitive api.diccionarios;

    exports com.curso.diccionario.ui.consola.api;
}
