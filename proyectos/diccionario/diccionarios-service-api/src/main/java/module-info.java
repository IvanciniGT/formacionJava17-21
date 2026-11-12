// Java 9: módulo de la plataforma (JPMS): qué necesita (requires), qué deja ver (exports) y qué servicios usa u ofrece (uses / provides).
module api.service.diccionarios {
    // transitive: quien use el servicio recibe Significado en sus respuestas, así que también
    // tiene que poder leer el API de diccionarios sin pedirlo él.
    requires transitive api.diccionarios;
    requires static lombok;

    exports com.curso.diccionario.service.api;
}
