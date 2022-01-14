package be.rlab.training

fun main() {

    class Planta {
        var tipo: String = "sucus"
        var temperatura: Int = 32
    }
    val plantaInterior = Planta()

    plantaInterior.tipo = "interior"
    plantaInterior.temperatura = 20

    val plantaTropical = Planta()

    plantaTropical.tipo = "tropical"
    plantaTropical.temperatura = 25

    val plantaSucus = Planta()

    plantaSucus.tipo = "sucus"
    plantaSucus.temperatura = 32

    val tipos: List<String> = listOf("interior", "tropical", "sucus")

    var plantInput: String

    do {
        print("ingrese el tipo de planta: ")
        plantInput = readLine()!!.trim().lowercase()

        if (plantInput !in tipos) {
            println("ingrese un tipo de planta valido: $tipos")
        }
    } while(plantInput !in tipos)

    val plant: String = plantInput

    print("ingrese la temperatura: ")
    val tempInput: String = readLine()!!
    val temp: Int = tempInput.toInt()

    when {
        plant == plantaInterior.tipo && (temp > plantaInterior.temperatura) -> {
            println(" /regar tropical/ ")
        }
        plant == plantaTropical.tipo && temp > plantaTropical.temperatura -> {
            println(" /regar interior/ ")
        }
        plant == plantaSucus.tipo && temp > plantaSucus.temperatura -> {
            println(" /regar sucus/ ")
        }
        else -> {
            println("no regar $plantInput")
        }
    }
}
