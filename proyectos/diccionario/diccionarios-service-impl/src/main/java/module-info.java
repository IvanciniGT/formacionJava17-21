// Java 9: módulo de la plataforma (JPMS): qué necesita (requires), qué deja ver (exports) y qué servicios usa u ofrece (uses / provides).
module impl.service.diccionarios {
    requires api.service.diccionarios;
    requires api.diccionarios;
    requires static lombok;

    // El servicio necesita un suministrador de diccionarios, y no sabe cuál: el que haya.
    uses com.curso.diccionario.api.SuministradorDeDiccionarios;

    // Sin exports: nadie importa esta clase. La app de consola la recibe del ServiceLoader, y el
    // servidor (que no usa el module path) la crea con su constructor.
    provides com.curso.diccionario.service.api.BuscadorDePalabras
            with com.curso.diccionario.service.impl.BuscadorDePalabrasEnDiccionarios;
}
