package be.rlab.training.lesson8

import org.joda.time.DateTime

class Vehicle1(
    val carbonPrint: Double
)

// The colon here means "extends from" and it defines an inheritance relationship
// between the class Car and the class Vehicle.
class Car1(
    val vehicle: Vehicle1,
    val doors: Int
)

// the open keyword means "this class is open for extension" (inheritance).
// This keyword is mandatory to inherit from this class.
open class Vehicle2(
    val carbonPrint: Double,
    protected var lastService: DateTime = DateTime.now()
)

// Inheritance is an identity relationship. We say that the Car IS a Vehicle.
//
class Car2(
    val doors: Int
) : Vehicle2(carbonPrint = 50.0) {
    fun runService() {
        if (lastService.plusMonths(6).isAfterNow) {
            println("car service required!")
        }
    }
}

// Inheritance is an identity relationship. We say that the Bike IS a Vehicle.
// We also can say that both a Bike AND Car2 ARE a Vehicle.
class Bike(
    val wheelSize: Int
) : Vehicle2(carbonPrint = 2.0)

// Functions might have the same name if they have different parameter types.
fun showCarbonPrintCar(car: Car2) {
    println(car.carbonPrint)
}

fun showCarbonPrintBike(bike: Bike) {
    println(bike.carbonPrint)
}

fun showCarbonPrintVehicle(vehicle: Vehicle2) {
    println(vehicle.carbonPrint)
}

fun main() {
    // This form of encapsulation and code re-use is called "composition".
    // The Car1 class IS NOT a Vehicle, it CONTAINS a Vehicle.
    val renault12: Car1 = Car1(doors = 5, vehicle = Vehicle1(carbonPrint = 50.0))
    val renault9: Car2 = Car2(doors = 5)
    val olmo: Bike = Bike(wheelSize = 5)

    showCarbonPrintCar(car = renault9)
    showCarbonPrintBike(bike = olmo)

    // Polymorphism is the property of an object to act as different types.
    // As renault9 and olmo are both Vehicle2, we can pass them as parameter to
    // any function that requires a Vehicle2 type.
    showCarbonPrintVehicle(vehicle = renault9)
    showCarbonPrintVehicle(vehicle = olmo)

    println("renault12 carbon print ${renault12.vehicle.carbonPrint}")
    println("renault9 carbon print ${renault9.carbonPrint}")
    println("olmo carbon print ${olmo.carbonPrint}")

    println("renault12 is Car1? ${renault12 is Car1}")
    println("renault9 is Car2? ${renault9 is Car2}")
    println("renault9 is Vehicle2? ${renault9 is Vehicle2}")
    println("olmo is Bike? ${olmo is Bike}")
    println("olmo is Vehicle2? ${olmo is Vehicle2}")
//    println("renault9 last service ${renault9.lastService}")
}
