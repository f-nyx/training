package be.rlab.training

fun main() {

    val tipo1: String = "tropical"
    val tipo2: String = "interior"
    val tipo3: String = "sucus"
    val tipos: List<String> = listOf(tipo1, tipo2, tipo3)

    var plantInput: String
    do {
        print("ingrese el tipo de planta: ")
        plantInput = readLine()!!.trim().lowercase()

        if (plantInput !in tipos) {
            println("ingrese un tipo de planta valido: $tipos")
        }
    } while(plantInput !in tipos)

    val plant: String = plantInput

    val mintemptropical: Int = 20
    val mintempinterior: Int = 25
    val mintempsucus: Int = 32

    print("ingrese la temperatura: ")
    val tempInput: String = readLine()!!
    val temp: Int = tempInput.toInt()

    when {
        plant == tipo1 && temp > mintemptropical -> {
            println(" /regar tropical/ ")
        }
        plant == tipo2 && temp > mintempinterior -> {
            println(" /regar interior/ ")
        }
        plant == tipo3 && temp > mintempsucus -> {
            println(" /regar sucus/ ")
        }
        else -> {
            println("no regar $plantInput")
        }
    }
}
