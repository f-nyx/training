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

}