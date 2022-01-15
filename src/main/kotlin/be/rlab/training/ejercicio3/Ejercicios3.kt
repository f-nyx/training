package be.rlab.training.ejercicio3

class Planta {
    var tipo: String = "sucus"
    var temperatura: Int = 32
}

fun pedirPlanta(plantas: List<Planta>): String {

    var plantInput: String

    do {
        print("ingrese el tipo de planta: ")
        plantInput = readLine()!!.trim().lowercase()

        fun tipoValido(item: Planta): Boolean {
            return plantInput == item.tipo
        }

        val isValid: Boolean = plantas.any(::tipoValido)
        val isValid2: Boolean = plantas.any { item: Planta ->
            // el return de un lambda es la ultima expresion de la funcion
            plantInput == item.tipo
        }

        if (plantas.any { item: Planta -> plantInput == item.tipo } == false) {

            val tiposValidos: String = plantas.joinToString { planta: Planta -> planta.tipo }
            println("ingrese un tipo de planta valido: $tiposValidos")
        }
    } while(isValid == false)

    return plantInput
}

fun main() {

    val plantaInterior = Planta()

    plantaInterior.tipo = "interior"
    plantaInterior.temperatura = 20

    val plantaTropical = Planta()

    plantaTropical.tipo = "tropical"
    plantaTropical.temperatura = 25

    val plantaSucus = Planta()

    plantaSucus.tipo = "sucus"
    plantaSucus.temperatura = 32

    val plantas: List<Planta> = listOf(plantaInterior, plantaTropical, plantaSucus)

    // las funciones son una forma de abstraer y encapsular comportamiento
    // Esto significa que lo podemos reutilizar desde diferentes lugares.
    val plantInput: String = pedirPlanta(plantas)

    val plant: String = plantInput

    print("ingrese la temperatura: ")
    val tempInput: String = readLine()!!
    val temp: Int = tempInput.toInt()

    when {
        plant == plantaInterior.tipo && (temp > plantaInterior.temperatura) -> {
            println(" /regar interior/ ")
        }
        plant == plantaTropical.tipo && temp > plantaTropical.temperatura -> {
            println(" /regar tropical/ ")
        }
        plant == plantaSucus.tipo && temp > plantaSucus.temperatura -> {
            println(" /regar sucus/ ")
        }
        else -> {
            println("no regar $plantInput")
        }
    }
}
