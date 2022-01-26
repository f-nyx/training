@file:Suppress("UNREACHABLE_CODE")

package be.rlab.training.ejercicios4

import be.rlab.training.Seasons
import org.joda.time.DateTime

class Planta(
    val nombre: String,
    val tipo: String,
    val luz: String
) {
    private var lastTime: DateTime = DateTime.now()

    fun water(): Boolean {
        val mustWater: Boolean = lastTime.plusSeconds(5).isAfterNow
        if (mustWater) {
            lastTime = DateTime.now()
        }
        return mustWater
    }
}

class Aromatica(
    val name: String,
    val season: String,
)

fun pedirTipo(plantas: List<Planta>): String {
    var tipoDePlantaInput: String

    do {
        println("¿Colocará la planta en interior o exterior?")
        tipoDePlantaInput = readLine()!!.trim().lowercase()
        val isValid: Boolean = plantas.any { item: Planta ->
            tipoDePlantaInput == item.tipo
        }

        if (isValid == false) {
            val tiposValidos: String = plantas
                .distinctBy { planta: Planta -> planta.tipo }
                .joinToString { planta: Planta -> planta.tipo }
            println("Las opciones son: $tiposValidos")
        }

    } while (isValid == false)
    return tipoDePlantaInput
}


fun pedirCondicionesLuz(plantas: List<Planta>): String {
    var condicionesLuzInput: String
    do {
        println("Cuales son las codiciones de luz?")
        condicionesLuzInput = readLine()!!.trim().lowercase()
        val isValid: Boolean = plantas.any { item: Planta ->
            condicionesLuzInput == item.luz
        }
        if (isValid == false) {
            val condicionesLuzValidos: String = plantas
                .distinctBy { planta: Planta -> planta.luz }
                .joinToString { planta: Planta -> planta.luz }
            println("Las opciones son: $condicionesLuzValidos")
        }

    } while (isValid == false)
    return condicionesLuzInput
}

fun waterIfRequired(plants: List<Planta>) {
    plants.forEach { plant: Planta ->
        val watered: Boolean = plant.water()
        println("watered: $watered")
    }
    Thread.sleep(6000)
    plants.forEach { plant: Planta ->
        val watered: Boolean = plant.water()
        println("watered: $watered")
    }
}

fun main() {
    val ceropegia = Planta(nombre = "ceropegia", tipo = "interior", luz = "mucha")
    val croton = Planta(nombre = "croton", tipo = "interior", luz = "mucha")
    val dracena = Planta(nombre = "dracena", tipo = "interior", luz = "poca")
    val potus = Planta(nombre = "potus", tipo = "interior", luz = "poca")
    val begonia = Planta(nombre = "begonia", tipo = "exterior", luz = "n/a")
    val verbena = Planta(nombre = "verbena", tipo = "exterior", luz = "n/a")
    val violeta = Planta(nombre = "violeta", tipo = "exterior", luz = "n/a")
    val plantas: List<Planta> = listOf(ceropegia, croton, dracena, potus, begonia, verbena, violeta)
    waterIfRequired(plantas)

    val albahaca = Aromatica(name = "albahaca", season = "spring")
    val ciboulette = Aromatica(name = "ciboulette", season = "spring")
    val romero = Aromatica(name = "romero", season = "spring-autumn")
    val oregano = Aromatica(name = "oregano", season = "spring-autumn")
    val lavandin = Aromatica(name = "lavandin", season = "spring-summer-autumn-winter")
    val melisa = Aromatica(name = "melisa", season = "spring-summer-autumn-winter")
    val aromaticas: List<Aromatica> = listOf(albahaca, ciboulette, romero, oregano, lavandin, melisa)

    val tipoDePlantaInput: String = pedirTipo(plantas = plantas)
    val condicionesLuzInput: String = pedirCondicionesLuz(plantas = plantas)

    val resolvedPlants: List<Planta> = plantas.filter { plant: Planta ->
        plant.tipo == tipoDePlantaInput && plant.luz == condicionesLuzInput
    }
    val catalogo: String = resolvedPlants.joinToString { plant: Planta ->
        plant.nombre
    }

    val currentSeason: String = Seasons.random()

    val resolvedAromaticas: List<Aromatica> = aromaticas.filter { aromatica: Aromatica ->
        currentSeason in aromatica.season
    }

    val aromaticasEstacionales: String = resolvedAromaticas.joinToString {aromatica: Aromatica ->
        aromatica.name
    }

    println("Plantas disponibles: ${catalogo}. También están disponibles las siguientes aromaticas: ${aromaticasEstacionales}")
}
