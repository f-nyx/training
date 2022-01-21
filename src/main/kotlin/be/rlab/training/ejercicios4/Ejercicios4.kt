@file:Suppress("UNREACHABLE_CODE")

package be.rlab.training.ejercicios4

//4 plantas de interior, 2 mucha luz (ceropegia, croton), y 2 poca luz (dracena, potus)
//3 plantas de exterior (begonia, verbena, violeta)
//preguntar al usuario: Colocará la planta en interior o exterior? Si responde interior, El espacio
// cuenta con poca luz o mucha luz?

class Planta(
    val nombre: String,
    val tipo: String,
    val luz: String
)


fun main() {
    val ceropegia = Planta(nombre = "ceropegia", tipo = "interior", luz = "mucha")
    val croton = Planta(nombre = "croton", tipo = "interior", luz = "mucha")
    val dracena = Planta(nombre = "dracena", tipo = "interior", luz = "poca")
    val potus = Planta(nombre = "potus", tipo = "interior", luz = "poca")
    val begonia = Planta(nombre = "begonia", tipo = "exterior", luz = "n/a")
    val verbena = Planta(nombre = "verbena", tipo = "exterior", luz = "n/a")
    val violeta = Planta(nombre = "violeta", tipo = "exterior", luz = "n/a")

    val plantas: List<Planta> = listOf(ceropegia, croton, dracena, potus, begonia, verbena, violeta)


    val tipoDePlantaInput: String = readLine()!!.trim().lowercase()

    val intensidadLuzInput: String = readLine()!!.trim().lowercase()

    fun pedirTipo(tipo:String): String {
        do {
            println("¿Colocará la planta en interior o exterior?")

            fun tipoValido(item: Planta): Boolean {
                return tipoDePlantaInput == item.tipo
            }
            val isValid: Boolean = plantas.any(::tipoValido)

            if (plantas.any { item: Planta -> tipoDePlantaInput == item.tipo } == false) {

                val tiposValidos: String = plantas.joinToString { planta: Planta -> planta.tipo }
                println("Las opciones son: $tiposValidos")
            }

        } while(isValid == false)

        val esInterior: Boolean = tipoDePlantaInput == "interior"

       do {
           fun pedirIntensidad(): String {
               do {
                   println("¿El espacio cuenta con poca luz o mucha luz?")

                   fun intensidadValido(item: Planta): Boolean {
                       return intensidadLuzInput == item.luz
                   }

                   val isValid: Boolean = plantas.any(::intensidadValido)

                   if (plantas.any { item: Planta -> intensidadLuzInput == item.luz } == false) {

                       val luzValidos: String = plantas.joinToString { planta: Planta -> planta.luz }
                       println("Las opciones son: $luzValidos")
                   }
                   return intensidadLuzInput

               } while (isValid == false)

       }
           while (esInterior == true)
               return intensidadLuzInput


           val tipoDePlanta: String = pedirTipo(tipo)

           val intensidadLuz: String = pedirIntensidad()

    fun filterPlantaInteriorPocaLuz(planta: Planta): Boolean {
         return planta.luz == "poca"
           }
    val plantasDeInteriorPocaLuz = plantas.filter(::filterPlantaInteriorPocaLuz)


    fun filterPlantaInteriorMuchaLuz(planta: Planta): Boolean {
         return planta.luz == "mucha"
           }
    val plantasDeInteriorMuchaLuz = plantas.filter(::filterPlantaInteriorMuchaLuz)


    fun filterPlantaExterior(planta: Planta): Boolean {
         return planta.tipo == "exterior"
           }
    val plantasDeExterior = plantas.filter(::filterPlantaExterior)

    when {
          tipoDePlanta == "interior" && (intensidadLuz == "poca") -> {
          println("Las plantas disponibles son: $plantasDeInteriorPocaLuz")
               }
          tipoDePlanta == "interior" && (intensidadLuz == "mucha") -> {
          println("Las plantas disponibles son: $plantasDeInteriorMuchaLuz")
               }
          tipoDePlanta == "exterior" -> {
          println("Las plantas disponibles son: $plantasDeExterior")
               }
          }

       }}}

