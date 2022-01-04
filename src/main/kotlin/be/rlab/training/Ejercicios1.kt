package be.rlab.training

fun main() {

    val tipo1: String = "tropical"
    val tipo2: String = "interior"
    val tipo3: String = "sucus"

    val plantInput: String = readLine()!!.toString()
    val plant: String = plantInput

    val case1: Boolean = plant == tipo1
    val case2: Boolean = plant == tipo2
    val case3: Boolean = plant == tipo3

    if (case1) println(" /regar diariamente/ ")
    if (case2) println(" /regar día por medio/ ")
    if (case3) println(" /regar semanalmente/ ")



    val mintemptropical: Int = 20
    val mintempinterior: Int = 25
    val mintempsucus: Int = 32

    val tempInput: String = readLine()!!
    val temp: Int = tempInput.toInt()

    val casetemp1: Boolean = temp > mintemptropical
    val casetemp2: Boolean = temp > mintempinterior
    val casetemp3: Boolean = temp > mintempsucus

    if (casetemp1) {
        print(" /regar tropical/ ")
    } else {
        print(" /no regar tropical/ ")
    }

    if (casetemp2) {
        print(" /regar interior/ ")
    } else {
        print(" /no regar interior/ ")
    }

    if (casetemp3) {
        print(" /regar sucus/ ")
    } else {
        print(" /no regar sucus/ ")
    }
}