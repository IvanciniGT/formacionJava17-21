
# Proyecto de diccionarios

Versión 1: App consola que usa ficheros para albergar las palabras.
    Limitaciones importantes:
    - No puedo garantizar en la empresa que todos los usuarios tengan la misma versión instalada (o asegurarlo es un trabajón adicional)
      -> El coste de operaciones (gestión de incidencias, actualizaciones...) se dispara
      -> Distintos usuarios pueden tener versiones diferentes de la app, lo que genera inconsistencias
      -> Cualuiqer cambio (a cualquier nivel: funcionalidad, interfaz gráfica, palabras/idiomas) requiere que todos los usuarios actualicen, lo que es complicado de coordinar
      ...

    Arquitectura basada en componentes desacoplados / módulos / subproyectos.
    El objetivo es tener bloques/trozos/grupos de código independientes que puedan ser reemplazados / reusados sin afectar al resto del sistema.
    Esto me permitirá ir evolucionando el sistema con el menor impacto posible:
    - Cantidad de código que debo modificar:
          - Nuestro objetivo sería NO TENER QUE MODIFICAR NI UNA SOLA LINEA DE CODIGO CUANDO LLEGUEN CAMBIOS
          - Puedo crear código nuevo
          - Puedo borrar código que hay
          - PERO NO QUIERO MODIFICAR NI UNA LINEA DE CODIGO
          - Si consigo esto, la probabilidad de introducir errores nuevos sobre el código que YA ESTABA OPERATIVO será muy baja
            Esto es un objetivo deseable/ideal... No siempre podremos alcanzarlo al 100%, pero nos sirve como guía para diseñar la arquitectura del sistema.

            Vamos a montar varios módulos:
                - La parte de gestión de diccionarios:              Lidia con la gestión de las palabras, significados, idiomas...
                    - Este componentes es conceptualmente un repositorio: LOGICA DE PERSISTENCIA
                    - diccionarios-api.jar                                                                                              v1.0.0
                    - diccionarios-impl-ficheros.jar                                                                                    v1.0.0    
                - Interfaz gráfica:                                 Lidia con la interacción con el usuario: Recoje datos y ofrece información visualmente
                  - ui-api.jar                                                                                                          v1.0.0
                  - ui-impl-consola.jar                                                                                                 v1.0.0
                - Servicio de búsqueda:                             Lidia con la búsqueda de palabras en los diccionarios
                  - Un servicio es un tipo de componente que define LOGICA DE NEGOCIO!
                  - diccionarios-service-api.jar                                                                                        v1.0.0
                  - diccionarios-service-impl.jar                                                                                       v1.0.0
                - Diccionarios (Modulos independientes... entre si  y con respecto al resto.)
                    - diccionario-es.jar                                                                                                v1.0.0
                    - diccionario-en.jar                                                                                                v1.0.0
                - La aplicación:                                    El ensamblado de todo
                  - diccionarios-app-consola-ficheros.jar                                                                               v1.0.0
                      Esta usa:
                        diccionarios-impl-ficheros.jar                                                                                  v1.0.0
                            diccionarios-api.jar                                                                                        v1.0.0
                        diccionarios-service-impl.jar                                                                                   v1.0.0
                            diccionarios-service-api.jar                                                                                v1.0.0
                        ui-impl-consola.jar                                                                                             v1.0.0
                            ui-api.jar                                                                                                  v1.0.0
                        diccionarios-es.jar                                                                                             v1.0.0
                        diccionarios-en.jar                                                                                             v1.0.0

A partir de aquí evolucionamos:
Potenciales cambios:
    - Nuevas palabras en español
       diccionario-es.jar                                                                                                               v1.1.0
    - Arreglas algunas palabras de inglés, que tienen typos en la sintaxis
       diccionario-en.jar                                                                                                               v1.0.1
    - Quiero cambiar los mensajes de ayuda de la app consola
       ui-impl-consola.jar                                                                                                              v1.0.1
Y Empaqueto una versión nueva de mi app de diccionario
       diccionarios-app-consola-ficheros.jar                                                                                            v1.1.0
                      Esta usa:
                        diccionarios-impl-ficheros.jar                                                                                  v1.0.0
                            diccionarios-api.jar                                                                                        v1.0.0
                        diccionarios-service-impl.jar                                                                                   v1.0.0
                            diccionarios-service-api.jar                                                                                v1.0.0
                        ui-impl-consola.jar                                                                                             v1.0.1
                            ui-api.jar                                                                                                  v1.0.0
                        diccionarios-es.jar                                                                                             v1.1.0
                        diccionarios-en.jar                                                                                             v1.0.1

ESTO ES HACER CONTROL DE CONFIGURACION SERIO DE MI SISTEMA! ASI QUIERO TRABAJAR.

DE AQUI vamos a pasar a otra cosa... UN SALTO GORDO!

Hasta este momento tenemos:

        Entorno local de un usuario
    -------------------------------------------------------------------------------------------------------------------------------------------------------
        diccionarios-app-consola-ficheros.jar -> diccionarios-service-api -> diccionarios-service-impl -> diccionarios-api -> diccionarios-impl-ficheros
                                                                                                                                    -> diccionarios-es 
                                                                                                                                    -> diccionarios-en
                                              -> ui-api -> ui-impl-consola

Queremos llevar la gestión de diccionarios a un servidor (ARQUITECTURA DISTRIBUIDA CLIENTE-SERVIDOR)
Para qué?
- Si hay cambios en las palabras/diccionarios no tener que reinstalarlos en todas las máquinas de los usuarios
- Si hay cambios en el COMO USO los diccionarios (servicio) no tener que actualizar todas las aplicaciones cliente
Cómo lo hago?

        Entorno local de un usuario                                                                 Entorno backend
    -------------------------------------------------------------------------------------------------------------------------------------------------------
        dic-app-consola-ficheros.jar -> dic-service-api -> dic-service-impl-rest -> http ->  dic-controlador-rest  -> dic-service-api 
                                                                                                                        -> dic-service-impl -> dic-api -> dic-impl-ficheros
                                                                                                                                                -> dic-es 
                                                                                                                                                -> dic-en
                                     -> ui-api -> ui-impl-consola


    dic-service-impl-rest -> Traduce peticiones al api JAVA a peticiones HTTP
        recibe una llamada a la función JAVA: buscarSignificados
        la traduce a una petición HTTP correspondiente
            /buscarSignificados?palabra=...&idioma=...

NO HAY QUE CAMBIAR NADA DEL CODIGO. Solo hay que crear 2 componentes nuevos:
    - dic-controlador-rest (que define unos endpoints para exponer vía protocolo REST las funciones de nuestro servicio en backend)             v1.0.0
    - dic-service-impl-rest (que traduce las llamadas a la API JAVA a peticiones HTTP REST correspondientes)                                    v1.0.0
     Reaprovecho TODO el código que tengo... TODO
     Solo creo 2 cosas nuevas. Beneficios:
      - Las probabilidades de CAGARLA son bajas, ya que no estamos tocando el código existente, solo añadiendo dos componentes nuevos.
      - Son 2 componentes nuevos, que puedo probar UNITARIAMENTE de forma intensiva
      - Sigo teniendo la posibilidad de ejecutar el sistema en modo 100% local o en modo distribuido vía HTTP:
                dic-app-consola-servidor.jar                                                                                                    v1.0.0
                        dic-service-impl-rest.jar                                                                                       v1.0.0
                            dic-service-api.jar                                                                                         v1.0.0
                        ui-impl-consola.jar                                                                                             v1.0.1
                            ui-api.jar                                                                                                  v1.0.0
                dic-app-servidor.jar                                                                                                            v1.0.0
                        diccionarios-impl-ficheros.jar                                                                                  v1.0.0
                            diccionarios-api.jar                                                                                        v1.0.0
                        diccionarios-service-impl.jar                                                                                   v1.0.0
                            diccionarios-service-api.jar                                                                                v1.0.0  
                        dic-controlador-rest-api                                                                                        v1.0.0
                            dic-controlador-rest                                                                                        v1.0.0
                        diccionarios-es.jar                                                                                             v1.1.0
                        diccionarios-en.jar                                                                                             v1.0.1
        Y en paralelo sigo teniendo:
                diccionarios-app-consola-ficheros.jar                                                                                           v1.1.0
                      Esta usa:
                        diccionarios-impl-ficheros.jar                                                                                  v1.0.0
                            diccionarios-api.jar                                                                                        v1.0.0
                        diccionarios-service-impl.jar                                                                                   v1.0.0
                            diccionarios-service-api.jar                                                                                v1.0.0
                        ui-impl-consola.jar                                                                                             v1.0.1
                            ui-api.jar                                                                                                  v1.0.0
                        diccionarios-es.jar                                                                                             v1.1.0
                        diccionarios-en.jar                                                                                             v1.0.1

Y creando 3 cositas.... tengo el cambio hecho.
Ahora, que ya tengo eso en el servidor... (YA ES UNA MEJORA GORDA) me planteo el mover las cosas a BBDD. Qué necesito?


        Entorno local de un usuario                                                                 Entorno backend
    -------------------------------------------------------------------------------------------------------------------------------------------------------
        dic-app-consola-ficheros.jar -> dic-service-api -> dic-service-impl-rest -> http ->  dic-controlador-rest  -> dic-service-api 
                                                                                                                        -> dic-service-impl -> dic-api -> dic-impl-ficheros
                                                                                                                                                -> dic-es 
                                                                                                                                                -> dic-en
                                     -> ui-api -> ui-impl-consola

    Tengo que cambiar dic-impl-ficheros? No internamente.. Ese lo que tengo es que SACARLO del ensamblado... y REEMPLAZARLO POR UNO NUEVO

        Entorno local de un usuario                                                                 Entorno backend
    -------------------------------------------------------------------------------------------------------------------------------------------------------
        dic-app-consola-ficheros.jar -> dic-service-api -> dic-service-impl-rest -> http ->  dic-controlador-rest  -> dic-service-api 
                                                                                                                        -> dic-service-impl -> dic-api -> dic-impl-bbdd

                                                                                         -> cargador-de-bbdd-palabras
                                                                                                -> dic-es 
                                                                                                -> dic-en
                                     -> ui-api -> ui-impl-consola

    Y de donde saca ese los datos? Cómo populo la BBDD?
        - Creo un componente nuevo llamado: cargador-de-bbdd-palabras
        - Ese lo que hace es al arrancar el servidor, revisa si la BBDD está vacía. Si lo está, la llena con los datos iniciales de los diccionarios.
          Es más, podemos hacerlo un poco más listo...
            1. Si el número de palabras en un fichero de un idioma es distinto del cargado actualmente en BBDD:
               1. Que se borren las palabras de ese idioma
               2. Que se carguen las palabras desde el fichero correspondiente.
    Así tenemos el control de versiones de los diccionarios y nos aseguramos de que la BBDD siempre esté sincronizada con los ficheros de origen.
    Queda así:
                dic-app-servidor.jar                                                                                                            v2.0.0
                        diccionarios-impl-bbdd.jar                                                                                      v1.0.0
                            diccionarios-api.jar                                                                                        v1.0.0
                        diccionarios-service-impl.jar                                                                                   v1.0.0
                            diccionarios-service-api.jar                                                                                v1.0.0  
                        dic-controlador-rest-api                                                                                        v1.0.0
                            dic-controlador-rest                                                                                        v1.0.0
                        cargador-de-bbdd-palabras                                                                                       v1.0.0
                            diccionarios-es.jar                                                                                         v1.1.0
                            diccionarios-en.jar                                                                                         v1.0.1

    Qué versión de la app de servidor genero? dic-app-servidor.jar  v1.0.0 -> v2.0.0
        HEMOS METIDO UN BREAKING CHANGE: La dependencia con otro componente que ahí no viene pintado: BBDD
        La versión anterior necesitaba BBDD? NO
        La nueva versión necesita BBDD? SÍ... Por ende.. la nueva versión PUEDE FUNCIONAR en el mismo entorno donde funcionaba la version anterior?
            NO... necesito uin entorno que disponga de BBDD.

PERO NO QUEDA AQUI... aún nos queda un cambio por hacer. FUNCIONAL!
Ofrecer sugerencias si una palabra no existe.
    Dame los significados de la palabra "manana" ... y que me diga el sistema:
    - La palabra manana no existe en espàñol.. Quiso decir? "mañana", "manzana", "ananá"
Qué implica este cambio?
LO QUE TENEMOS. De dónde partimos:

                dic-app-consola-servidor.jar                                                                                                    v1.0.0
                        dic-service-impl-rest.jar                                                                                       v1.0.0
                            dic-service-api.jar                                                                                         v1.0.0
                        ui-impl-consola.jar                                                                                             v1.0.1
                            ui-api.jar                                                                                                  v1.0.0

                dic-app-servidor.jar                                                                                                            v2.0.0
                        diccionarios-impl-bbdd.jar                                                                                      v1.0.0
                            diccionarios-api.jar                                                                                        v1.0.0
                        diccionarios-service-impl.jar                                                                                   v1.0.0
                            diccionarios-service-api.jar                                                                                v1.0.0  
                        dic-controlador-rest-api                                                                                        v1.0.0
                            dic-controlador-rest                                                                                        v1.0.0
                        cargador-de-bbdd-palabras                                                                                       v1.0.0
                            diccionarios-es.jar                                                                                         v1.1.0
                            diccionarios-en.jar                                                                                         v1.0.1

Qué hay que modificar? Por donde empezamos?
 - api servicio                                                                                                                         v.1.1.0
     nueva función? Depende. Opciones:
     - Nuevo método para sugerencias
     - Que el existente si NoExisteLaPalabra que ofrezca las sugerencias:
       Teníamos un objeto(clase) llamada PalabraNoEncontrada, que podría ampliarse para incluir sugerencias de palabras similares.
    Cuál hacemos?
        Lo ideal, modificar lo que hay... pero con mucho cuidado de no introducir BREAKING CHANGES en el API (aquí nos vendrá guay los default methods)
        Haremos nuestro mejor esfuerzo para no romper el API existente. En nuestro caso no nos harán falta lo default... realmente solo tocamos una CLASE de transporte de datos del api.
 - impl servicio... pero cuál? HABIA 2? LAS 2
                        dic-service-impl-rest.jar                                                                                       v1.1.0
                        diccionarios-service-impl.jar                                                                                   v1.1.0
 - También el controlador:
                        dic-controlador-rest-api                                                                                        v1.1.0
                        dic-controlador-rest                                                                                            v1.1.0
 - A nivel de UI mostrar las sugerencias de palabras similares
                        ui-impl-consola.jar                                                                                             v1.1.0
                            ui-api.jar                                                                                                  v1.1.0

Este es un cambio que toca un huevo de componentes. La ventaja, que está muy claro qué debo tocar en cada componente.
E iremos por orden


---
Cuando tengamos todo esto, desarrollaremos un frontal WEB, ANGULAR / TS. Y quitaremos nuestra app de consola... o no la quitaremos.. irá en paralelo...
Ambas deben poder seguir usándose... no son incompatibles.

    Además de la 

                dic-app-consola-servidor.jar

                dic-app-servidor.jar        

                dic-app-web-angular.zip (llena de htmls... y js, y css) que se pondrá dentro de un servidor de apps... 
                quizás nuestra propia: dic-app-servidor.jar        











---

diccionarios-api.jar
    interface Diccionario
        Optional<List<String>> getSignificados(String palabra);

    interface SuministradorDeDiccionarios
        Optional<Diccionario> getDiccionario(String idioma);

    Con su impl. a ficheros. 
    Esto son funciones de muy bajo nivel. Casí (y tendrán su reflejo tal cual) hacen operaciones CRUD a una BBDD... en nuestro caso solo selects.
        SELECT * FROM diccionarios WHERE idioma = ?;
        SELECT significado FROM palabras WHERE palabra = ? AND diccionario_id = ?;

diccionarios-service-api
    interface BuscadorDePalabras
        ResultadoDeBusqueda buscarExacta(String palabra, String idioma);
         ResultadoDeBusqueda: PalabraEncontrada, PalabraNoEncontrada, IdiomaNoSoportado
            Pero este no es solo un agregado de las funciones del diccionarios-api. Aporta lógica de negocio.
        ResultadoDeBusqueda buscarIgnorandMayusculasYAcentos(String palabra, String idioma);



MEMBRILLO      membrillo
Atun           atún


---

# MAVEN

Vamos a usar MAVEN. Qué es maven?

MAVEN NO ES UN GESTOR DE DEPENDENCIAS. Eso es una visión muy reducida de lo que es maven.
Maven es una herramienta para automatizar trabajos en proyectos de software, como:
- Compilación del proyecto
- La ejecución de pruebas
- La generación de documentación
- La gestión de dependencias
- La creación de paquetes y despliegues
- La ejecución de mi código
- Mandar mi código a un Sonarqube
- Solicitar la generación de un informe de cobertura JACOCO
    COBERTURA: % del código que está siendo ejecutado por pruebas automatizadas, con independencia de que las pruebas sean correctas o no.
    En los proyectos hoy en día se exige un alto nivel de cobertura, normalmente superior al 80% o 90%.
    Se mide de varias formas:
    - cobertura de líneas: % de líneas de código ejecutadas por las pruebas.
    - cobertura de ramas: % de ramas de código ejecutadas por las pruebas. Caminos distintos que puede tomar el código


        miFuncion()
            tarea1
            condicional1
                tarea2
                tarea3
                ... tarea 49
            else
                tarea 51

    Imaginad que hago una prueba sobre el else.
        % Cobertura de ramas tengo? 50%
        % Cobertura de líneas tengo? 4% (tarea1, tarea 51). 2 de 51 líneas ejecutadas.
    
    Imaginad que hago una prueba sobre el if.
        % Cobertura de ramas tengo? 50%
        % Cobertura de líneas tengo? 50 de 51 líneas ejecutadas : 98%

En nuestro caso, para no complicarnos, en lo que os he preparado viene incluido un maven wrapper.
Eso es un proyecto oficial de maven, que ofrece scripts aptos para linux, macOS y Windows.
En lugar de ejecutar el comando mvn, que es la forma tradicional de usar maven, podemos usar los scripts del maven wrapper, que se llaman mvnw (en Linux y macOS) o mvnw.cmd (en Windows).
Estos scripts (que lo ofrecen la gente de maven) permiten ejecutar maven sin necesidad de tenerlo instalado previamente y de forma global en el sistema.
Lo que hacen es si no tengo maven, me lo descargan PARA ESTE PROYECTO! Y Además me ponen la versión que se haya definido, de forma que todo el mundo use la misma verisón de maven. Y ESTO ES IMPORTANTE

Maven no sabe hacer la "O" Con un canuto. Todo el trabajo que hace es delegado a plugins.
Hay plugins para compilar, para empaquetar, para ejecutar pruebas... para mandar a sonarqubve o generar informes de cobertura.

Y esos plugins tienen versiones. Cada versión de maven lleva por defecto un conjunto de versiones de plugins que se consideran compatibles y estables con esa versión de maven.

En nuestro caso, necesitamos asegurar una versión MINIMA especialmente del plugin de compilación, que soporte java 21.
Que tenáis un maven, no significa que ese maven funcione con nuestro proyecto... si tengo una versión viejita, funcionará bien con proyectos java 8, o java 11, pero no con java 21.


Pruebas de caja negra: Las que diseñamos sin tener en cuenta la implementación concreta de un componente
    Se centran en las entradas y salidas del componente, sin preocuparse de cómo se implementa internamente.
    Son las que definimos a nivel del API

    Tenemos un api de diccionarios.
    Con una función llamada `boolean existe(Palabra palabra);`
    Si yo llamo y tengo configurado un diccionario de es, que si que tiene la palabra melón, 
    cuando ejecute esta función que debe devolver? TRUE
    Eso depende de la implementación?
        - Si tengo el diccionaario en BBDD o en ficheros ese comportamiento cambia? NO
        - Es una prueba que verifica el comportamiento del API sin importar la implementación interna.
        - Dicho de otra forma: CUALQUIER IMPLEMENTACION DEBE SUPERAR ESTAS PRUEBAS.
      OJO, las pruebas las defino a nivel del API, pero su ejecución se hace a nivel de cada implementación. 

Pruebas de caja blanca: Las que diseñamos teniendo ena cuenta la implementación concreta de un componente
    Se centran en la estructura interna del componente, verificando caminos específicos del código.
    Son las que definimos a nivel de implementación

    Por ejemplo, si quiero probar la función `existe` del diccionario, puedo diseñar pruebas que verifiquen caminos específicos del código, como:Imaginad que implemento nuestro paquete de diccionarios contra bbdd.
    Y la primera vez que pregunto por una palabra se hace la query.. pero además de devolverme el resultado, ese resultado es guardado localmente en una caché.
    Para que la siguiente vez que se pregunte por la palabra no sea necesario hacer la query a la BBDD y se pueda obtener el resultado directamente de la caché.

    La prueba de caja negra está probando la caché? NO.. solo preguntaba por la palabra melón 1 vez.
    Para probar la caché, necesitaríamos diseñar una prueba que consulte la misma palabra más de una vez y verifique que la segunda consulta no realiza la query a la BBDD, sino que obtiene el resultado directamente de la caché.

    Todas las impleemntaciones de nuestro módulo de diccionarios deben superar esta prueba?
        - NO, solo las que implementen la caché.
        - Esta prueba DEPENDE DE LA IMPLEMENTACION. Y se define dentro de ella... es una prueba de caja blanca!


---
                                    LOCAL
-------------------------------------------------------------------------------------------------------------
app-consola-integrada -> api-servicio -> impl-servicio-local -> api-diccionarios -> impl-diccionarios-fichero


        LOCAL                                                       SERVIDOR
------------------------------------------------------------- ---------------------------------------------------------------------------------------------------------------
app-consola-remoto -> api-servicio                      http                   api-servicio -> impl-servicio-local -> api-diccionarios -> impl-diccionarios-bbdd
                                 -> impl-servicio-servidor ->  -> contrrolador http ->
                                 Traducir el api a                  Traducir 
                                 peticiones http                   peticiones http al api java

                                                                    El controlador le monto en una app de backend dentro de un tomcat

                    app-web-angular ----------------> http

```java
String multilineaEnJava = "Esta es la primera línea\n" +
                           "Esta es la segunda línea\n" +
                           "Esta es la tercera línea";

String nuevoOperadorTextosMultilinea = """
                                   Esta es la primera línea
                                   Esta es la segunda línea
                                   Esta es la tercera línea
                                   """;
```


Antiguamente los hilos en Java correspondían con Hilos LOGICOS a nivel de SO.
Abrir un hilo de ejecución en un proceso (FORK) implicaba crear un nuevo hilo lógico que el sistema operativo gestionaba, y eso es muy costoso en términos de recursos y tiempo de creación.

En una app java podíamos abrir 100, 20, 300 hilos, pero cada uno de ellos consumía recursos significativos y el tiempo de creación era elevado.
Java o no Java... esto es algo de SO.

En Java 21 aparecen los hilos virtuales (Virtual Threads), que permiten crear miles de hilos de manera eficiente, ya que no están directamente ligados a los hilos del sistema operativo y su gestión es mucho más ligera.
Lo que se hace es una multiplexación de los hilos virtuales sobre un número reducido de hilos del sistema operativo, lo que permite manejar miles de hilos de manera eficiente sin el coste asociado a los hilos tradicionales.

Esto permite abrir millones de hilos virtuales sin que el sistema operativo se vea saturado, lo que facilita la concurrencia masiva en aplicaciones Java modernas.
Es más o menos el concepto dque hay en JS con los event loops y la ejecución asíncrona, donde no se bloquea el hilo principal y se pueden manejar muchas tareas concurrentes de manera eficiente. En Kotlin también existe: las corutinas, que permiten manejar la concurrencia de manera eficiente sin bloquear hilos del sistema operativo.

Es eso traído a Java con los hilos virtuales, que permiten manejar la concurrencia de manera eficiente sin depender directamente de los hilos del sistema operativo.
Pero muy bien hecho.
Es transparente totalmente.. solo es algo que activo a nivel de la JVM al crear los hilos.
Por ejemplo, en Java 21 se puede usar `Thread.ofVirtual().start(() -> { ... })` para crear un hilo virtual de manera sencilla y eficiente.