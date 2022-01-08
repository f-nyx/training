package be.rlab.training

/**
 * 1. Todos los tipos de datos en Kotlin son objetos
 * 2. Los objetos se construyen a partir de una clase que es un molde
 * 3. Las clases son los tipos de datos. Por ejemplo: String, List, Planta
 * 4. Los objetos tienen propiedades y comportamiento
 * 5. Se accede a las propiedades y funciones de un objeto usando el punto:
 * objeto.propiedad o objeto.funcion()
 *
 */

class Precio {
    var moneda: String = "pesos"
    var valor: Double = 0.0
}

// La clase Planta construye objetos de tipo Planta
// La clase es un molde y todos los objetos van a tener las propiedades y funciones
// que esten definidas en la clase.
class Planta {
    var estacion: String = "verano"
    // la propiedad precio puede contener un objeto de tipo precio
    var precio: Precio = Precio()
    var nombre: String = ""
}

// Un objeto agrupa propiedades y compoertamiento
object Lavanda {
    var estacion: String = "verano"
    var precio = Precio()
}

// Un objecto agrupa propiedades y compoertamiento
object Romero {
    var estacion: String = "verano"
    var precio = Precio()
}

// Un objecto agrupa propiedades y compoertamiento
object Tomillo {
    var estacion: String = "verano"
    var precio = Precio()
}

fun main() {
    val lavanda = Planta()
    val romero = Planta()
    val tomillo = Planta()
    val oregano = Planta()
    val melisa = Planta()
    val plantas: List<Planta> = listOf(lavanda, romero, tomillo, oregano, melisa)

    lavanda.nombre = "lavanda"
    romero.nombre = "romero"
    tomillo.nombre = "tomillo"
    oregano.nombre = "oregano"
    oregano.estacion = "primavera"
    melisa.nombre = "melisa"
    melisa.estacion = "otoño"

    // filter va a ejecutar la funcion una vez por cada elemento de la lista
    // filter va a pasar como parametro cada elemento de la lista
    fun filterSummerPlant(planta: Planta): Boolean {
        return planta.estacion == "verano"
    }

    // filter pide como parametro una funcion que reciba un solo parametro
    // del tipo de elemento de la lista, y que devuelva un valor de tipo Boolean
    val plantasDeVerano = plantas.filter(::filterSummerPlant)

    println("estacion oregano: ${oregano.estacion}")
    println("estacion melisa: ${melisa.estacion}")
    melisa.precio.valor = 10.0
    println("estacion oregano: ${oregano.estacion}")
    println("estacion melisa: ${melisa.estacion}")

    lavanda.precio.valor = 10.0
    println(lavanda.estacion)
    lavanda.estacion = "otoño"
    println(lavanda.estacion)
    println("${lavanda.precio.valor} ${lavanda.precio.moneda}")
}
