// Java 9: módulo de la plataforma (JPMS): qué necesita (requires), qué deja ver (exports) y qué servicios usa u ofrece (uses / provides).
module app {
    // El servicio trae con él el API de diccionarios (requires transitive): Significado.
    requires api.service.diccionarios;
    requires api.ui.consola;

    // Necesita que alguien, en tiempo de ejecución, aporte una implementación de cada cosa.
    // Ya no pide un suministrador de diccionarios: eso es cosa del servicio.
    uses com.curso.diccionario.service.api.BuscadorDePalabras;
    uses com.curso.diccionario.ui.consola.api.FabricaDeInterfacesDeUsuario;

    // Ya no lleva diccionarios (ni opens diccionarios): vienen en sus propios jars, diccionario-es, diccionario-en...
}
