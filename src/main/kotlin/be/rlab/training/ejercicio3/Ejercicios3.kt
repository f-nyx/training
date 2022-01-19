package be.rlab.training.ejercicio3

class Planta(tipoInicial: String, temperaturaInicial: Int) {
    var tipo: String = tipoInicial
    var temperatura: Int = temperaturaInicial
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

fun pedirTemperatura(): Int {

    print("ingrese la temperatura: ")
    val tempInput = readLine()!!

    return tempInput.toInt()
}

fun resolverSiRegar() {
    val plantaInterior = Planta(tipoInicial = "interior", temperaturaInicial = 20)
    val plantaTropical = Planta(tipoInicial = "tropical", temperaturaInicial = 25)
    val plantaSucus = Planta(tipoInicial = "sucus", temperaturaInicial = 32)
    val plantas: List<Planta> = listOf(plantaInterior, plantaTropical, plantaSucus)

    // las funciones son una forma de abstraer y encapsular comportamiento
    // Esto significa que lo podemos reutilizar desde diferentes lugares.
    val plantInput: String = pedirPlanta(plantas)

    val plant: String = plantInput

    val temp: Int = pedirTemperatura()

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

fun main() {
    resolverSiRegar()
}
