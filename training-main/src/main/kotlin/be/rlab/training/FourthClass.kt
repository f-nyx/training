package be.rlab.training

fun main() {
    // saltos incondicionales version kotlin
    fun case1(operando1: Int, operando2: Int) {
        println(operando1 + operando2)
    }

    val value1: Int = 20
    case1(value1, 10)

    // tipos de datos numericos:
    // Int, Long, Double (decimales)
    // tipos tienen jerarquia. Por ejemplo, los numeros tienen un tipo superior que se llama Number
    // Todos los tipos de datos, tanto primitivos como compuestos, tienen como super tipo Any
    val minUserAge: Int = 13
    // esto requiere ingresar un numero en la consola
    val ageInput: String = readLine()!!
    // el valor de ageInput debe contener un numero valido, sino va a generar un error
    val age: Int = ageInput.toInt()
    // syntax: el tipo de datos se especifica con dos puntos seguido del nombre del tipo de datos
    val foo: Boolean = age < minUserAge

    if (foo) {
        print("e muy chico")
    } else {
        print("it is good")
    }
}
