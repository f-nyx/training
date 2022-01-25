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

class Precio (
    var moneda: String,
    var valor: Double
)

// La clase Planta construye objetos de tipo Planta
// La clase es un molde y todos los objetos van a tener las propiedades y funciones
// que esten definidas en la clase.
class Planta(
    val estacion: String,
    val nombre: String,
    // la propiedad precio puede contener un objeto de tipo precio
    var precio: Precio
)

// Un objeto agrupa propiedades y comportamiento
object Lavanda {
    var estacion: String = "verano"
    var precio = Precio(moneda = "pesos", valor = 20.0)
}

// Un objecto agrupa propiedades y comportamiento
object Romero {
    var estacion: String = "verano"
    var precio = Precio(moneda = "pesos", valor = 25.0)
}

// Un objecto agrupa propiedades y comportamiento
object Tomillo {
    var estacion: String = "verano"
    var precio = Precio(moneda = "pesos", valor = 20.0)
}

fun main() {
    val lavanda = Planta(nombre = "lavanda", estacion = "verano", precio = Precio(moneda = "pesos", valor = 20.0))
    val romero = Planta(nombre = "romero", estacion = "verano", precio = Precio(moneda = "pesos", valor = 25.0))
    val tomillo = Planta(nombre = "tomillo", estacion = "verano", precio = Precio(moneda = "pesos", valor = 20.0))
    val oregano = Planta(nombre = "oregano", estacion = "primavera", precio = Precio(moneda = "pesos", valor = 20.0))
    val melisa = Planta(nombre = "melisa", estacion = "otoño", precio = Precio(moneda = "pesos", valor = 20.0))
    val plantas: List<Planta> = listOf(lavanda, romero, tomillo, oregano, melisa)

    if (plantas.contains(lavanda)) {
        println("la lista esta vacia")
    }

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
    println(lavanda.estacion)
    println("${lavanda.precio.valor} ${lavanda.precio.moneda}")
}
