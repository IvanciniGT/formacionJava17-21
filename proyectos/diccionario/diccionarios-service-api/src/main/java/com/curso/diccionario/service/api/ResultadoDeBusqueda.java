package com.curso.diccionario.service.api;

// Este interfaz solo existe porque Java no deja que una función devuelva tipos alternativos
// (en TypeScript sería: PalabraEncontrada | PalabraNoEncontrada | IdiomaNoSoportado).
// Y al hacer falta un tipo común, el contrato quedaría ABIERTO: cualquiera podría crear otra
// implementación de este interfaz y devolverla, y quien nos usa ni se enteraría (Liskov).
//
// Java 17: sealed / permits, la lista cerrada de quién puede implementar esto. Nadie más puede.
// Y de regalo, un switch sobre él es exhaustivo: si mañana se añade un caso al permits, cada
// switch que no lo trate deja de compilar.
//
    // Hay veces que una función puede devolver varias cosas DISTINTAS ENTRE SI.
    // O requerir al menos información adicional en alguna de ellas.
    // Y el Optional se queda pobre.
    // El Optional me pemite devolver algo o no!
    // Pero incluso si no lo devuelve, me impide saber porque no lo devuelvo.

    // doLogin(usuario, password) qué devuelve?
    // - Exito                                   |
    // - Fallo: usuario no existe                |
    // - Fallo: password incorrecta               > Esto son caminos posibles... No excepcionales.
    // - Fallo: cuenta bloqueada                 |
    // - Fallo: El sistema está en mantenimiento |
    // - Fallo: error desconocido de sistema        ---> Exception

    // en Typescript puedo hacer que una función devuelva muchas cosas diferentes:
    // ts
    // function doLogin(usuario: string, password: string): LoginExito | LoginFallido | SistemaEnMantenimiento {
    // }

    // en Java no se puede... En java necesito crear una base común a todos esos tipos:

    // interface LoginResultado {}
    // class LoginExito implements LoginResultado {}
    // class LoginFallido implements LoginResultado {}
    // class SistemaEnMantenimiento implements LoginResultado {}

    //java
    // public LoginResultado doLogin(String usuario, String password) {
    //     // implementación
    // }

    // Ahora bien...
    // Yo consumo ahora esa función. Qué espero que devuelva la función? LoginResultado
    // Ya... con que valores? Para que los trate en mi pantalla y muestre mensajes distintos: 
    //    LoginExito, LoginFallido, SistemaEnMantenimiento
    // Bien... Y si el día de mañana cambian el backend y crean:
    //    public interface CuentaBloqueada implements LoginResultado {}
    // Espero yo que estoy conumiendo el API ese valor nuevo: CuentaBloqueada? Ni de coña
    // Tengo un if para ese valor nuevo? Ni de coña.
    // Resultado: El sistema falla!

    // Y esto ha apsado por no respetar el princio de sustución de Liskov (Liskov Substitution Principle)
    // Que me dice que CIERRE EL PUÑETERO CONTRATO!

    // El problema gordo es COMO LECHES ME ENTERO YO DE QUE HAN METIDO UN NUEVO TIPO DE RESULTADO QUE NO ESPERABA.
    // CuentaBloqueada, por ejemplo, y yo ni me entero.

    // Y aparecen en JAVA los sealed classes (clases selladas) para resolver este problema:

    // public sealed interface LoginResultado permits LoginExito, LoginFallido, SistemaEnMantenimiento {}
    
    // Si alguien intentase crear una nueva clase que implemente LoginResultado sin declararla en el permits, el compilador lanzaría un error.
    // Nadie puede crear un CuentaBloqueada sin declararla en el permits.

    // Y si lo meten en el permits, entonces el compilador me dará error a mi al consumir:
    // switch (resultado) {  // switch con patrones de Java 21, que es exhaustivo sobre una sellada
    //     case LoginExito exito -> ...
    //     case LoginFallido fallido -> ...
    //     case SistemaEnMantenimiento mantenimiento -> ...
    // }

    // Y si meten uno nuevo, me da error de compilación = GUAY! Ya me he enterado... ya no explota mi sistema en silencio.
// Java 17: sealed / permits, la lista cerrada de quién puede implementar esto.
public sealed interface ResultadoDeBusqueda permits PalabraEncontrada, PalabraNoEncontrada, IdiomaNoSoportado {

    // Lo que tienen en común los tres: los records que lo implementan ya traen estos accesores.
    String palabra();

    String idioma();
}
