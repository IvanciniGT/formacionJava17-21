

def saluda(nombre):
    print("Hola " + nombre)

saluda("Menchu")

persona = "Federico"
#      ->
# Estoy asignando la variable persona a un valor de tipo string
# El concepto de variable CAMBIA DE LENGUAJE EN LENGUAJE. 
# En C, Fortran, ADA, una variables es como una CAJITA(Contenedor) donde pongo cosas.. para luego sacarlas). Es una ZONA DE MEMORIA donde se almacena el valor de la variable.
# En JAVA, C#, Python, JS, TS el concepto de variable es otro (al menos en gran parte). Una variable es una referencia a un dato que tengo en RAM. El concepto aquí tiene más que ver con el concepto de PUNTERO en C.

# Esa linea hace 3 cosas:
# - "Federico"    Esto crea un valor de tipo string en memoria, con dato: "Federico"
# - persona       Esto crea una variable llamada persona 
# - =             Esto asigna la variable al dato que tengo en RAM.

persona = "Menchu"
# - "Menchu"      Esto crea un valor de tipo string en memoria, con dato: "Menchu"
#                 Dónde se guarda Menchu, en el mismo sitio donde estaba "Federico" en memoria o en otro?
#                 En otro sitio distinto. Y Ahoa me encuentro con 2 datos de tipo str en memoria.
#                 Y luego cambio el postit de uno a otro. Reasigno la variable persona a un nuevo valor en memoria. 
#                 Lo que pasa que el dato "Federico" ha quedado huérfano de variable. En estos lenguajes ese dato es irrecuperable. Se convierte en basura.
#                 Y es posible (o no...npi) de que en un momento dado estre el RECOLECTOR DE BASURA elimine el dato "Federico" de la memoria si no hay más referencias a él.
#                 Esto pasa en Java, Python, JS...

miOtraVariable = saluda
# Ahora tengo una variable que apunta a una función. La función está en memoria RAM, como el dato "Menchu"
# He ejecutado aquñi saluda? No... no hay () detrás.. no he pasado argumento...

miOtraVariable("Carlos") # Ejecuto la función, a través de la variable.
# Cuando en lenguaje me permite que una variable apunte a una función para posteriormente ejecutar la función desde la variable, decimos que el lenguaje soporta el paradigma funcional.

# Pregunta evidente? Pa' que vale esta mierda?
# Lo imnportante no es lo que es la programación funcional (que es mu simple!!!)
# Lo importante es lo que puedo hacer una vez el lenguaje soporta esto:
# - Puedo crear funciones que acepten otras funciones como argumentos
# - Puedo crear funciones que devuelvan otras funciones como resultado (como los closures)

def doble(numero):
    return numero * 2

def multiplicar(numero1, numero2):
    return numero1 * numero2

# Ejemplos de operación: doblar, mitad, cuadrado, etc.
# Esto es una de las cosas que nos permite la programación funcional. Inyectar LOGICA en tiempo de ejecución a una función.
#def aplicar_operacion(numero, operacion):

print(doble(5))
print(multiplicar(3, 4))


def generar_saludo_formal(nombre):
    return f"Hola, {nombre}. Es un placer conocerte."

def generar_saludo_informal(nombre):
    return f"Qué pasa, {nombre}!"

def imprimir_saludo(saludo):
    print(saludo)

#imprimir_saludo(generar_saludo_formal("Carlos"))
#imprimir_saludo(generar_saludo_informal("Carlos"))

def imprimir_saludo_generado(funcion_generadora_de_saludos, nombre):
    saludo = funcion_generadora_de_saludos(nombre)
    print(saludo)

imprimir_saludo_generado(generar_saludo_formal, "Menchu")
imprimir_saludo_generado(generar_saludo_informal, "Menchu")

