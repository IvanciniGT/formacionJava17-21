// Java 9: módulo de la plataforma (JPMS): qué necesita (requires), qué deja ver (exports) y qué servicios usa u ofrece (uses / provides).
module api.diccionarios {
    // static: Lombok solo hace falta al compilar; @NonNull no llega al jar en tiempo de ejecución.
    requires static lombok;

    exports com.curso.diccionario.api;
}
