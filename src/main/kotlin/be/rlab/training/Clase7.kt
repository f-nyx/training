package be.rlab.training

class Car {
    var numberOfDoors: Int = 0
}

fun sum(number1: Int, number2: Int): Int {
    return number1 + number2
}

fun createCar(numberOfDoors: Int): Car {
    val newCar: Car = Car()
    newCar.numberOfDoors = numberOfDoors
    return newCar
}

fun messageWithPrefix(prefix: String): (String) -> String {
    // Cuando se anidan funciones, las funciones de mas adentro siempre ven los parametros y las variables
    // de las funciones que estan mas afuera.
    // Esto se llama Scope.
    val foo = "bar"

    fun concat(message: String): String {
        return "$foo - $prefix - $message"
    }
//    return { message: String ->
//        "$foo - $prefix - $message"
//    }
    return ::concat
}

fun main() {
    val resultado: Int = sum(10, 5)
    sum(number2 = 5, number1 = 10)
    val sumAlias: (Int, Int) -> Int = ::sum
    val resultado2: Int = sumAlias(10, 5)

    val sumAlias2: (Int, Int) -> Int = { numerito1: Int, param2: Int ->
        numerito1 + param2
    }
    val resultado3: Int = sumAlias2(10, 5)

    val numbers: List<Int> = listOf(1, 2, 3, 4, 5, 6)

    fun filterEven(item: Int): Boolean {
        return item % 2 == 0
    }

    fun scaleNumber(item: Int): Int {
        return item * 10
    }

    val evenNumbers1: List<Int> = numbers.filter(::filterEven)
    // Si se pasa una funcion como parametro y es el ultimo parametro, los parentesis de llamada son opcionales.
    val evenNumbers2: List<Int> = numbers.filter { item: Int ->
        item % 2 == 0
    }
    val scaledNumbers1: List<Int> = numbers.map(::scaleNumber)
    val scaledNumbers2: List<Int> = numbers.map { item ->
        item * 10
    }

    val sedan: Car = createCar(numberOfDoors = 5)
    val coupe: Car = createCar(numberOfDoors = 3)

    println(numbers)
    println(evenNumbers1)
    println(evenNumbers2)
    println(scaledNumbers1)
    println(scaledNumbers2)

    val createMessage: (String) -> String = messageWithPrefix("HELLO")
    println(createMessage("Alfi and Tales"))
}
