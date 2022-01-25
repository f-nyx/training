package be.rlab.training

fun main() {
    // saltos incondicionales version kotlin
    fun case1(operando1: Int, operando2: Int) {
        println(operando1 + operando2)
    }

    val value1: Int = 20
    case1(value1, 10)

    val minUserAge: Int = 13
    val ageInput: String = readLine()!!
    val age: Int = ageInput.toInt()
    val foo: Boolean = age < minUserAge

    if (foo) {
        print("e muy chico")
    } else {
        print("it is good")
    }
}


