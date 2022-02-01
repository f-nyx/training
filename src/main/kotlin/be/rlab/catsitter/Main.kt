import be.rlab.tehanu.Tehanu
import be.rlab.tehanu.annotations.Handler
import be.rlab.tehanu.clients.UpdateContext
import be.rlab.tehanu.clients.telegram.telegram
import com.sun.tools.javac.resources.CompilerProperties
import java.util.*

@Handler(name = "/say_hello")
fun sayHello(context: UpdateContext) {
    context.answer("hello!")
}

fun main(args: Array<String>) {
    println("starting catsitter bot")

    Tehanu.configure {
        handlers {
            register(::sayHello)
        }

        clients {
            telegram(System.getenv("TELEGRAM_TOKEN"))
        }
    }.start()
}

class Cats(
    var quantity: Int,
    var catName: String
)

class Client(
    val name: String,
    val phoneNumber: Number,
    val address : String,
    val cats: Cats
)
//Creo que debería ser una función -pricing- que devuelva -priceTotal-.
// En ese caso estas variables deberían estar dentro de Service?? o antes??
class Price(
    val catQuantity: Int,
    var totalVisits: Int,
    val zoneAditional: Int,
    val sundaysAndHolidaysAditional: Int,
    val longerVisitsAditional: Int,
    val keysDeliveryAditional: Int
)

class Payment(
    val type: Int,
    val reservation: Int
)

class Keys(
    val receivingDate: Date,
    val receivingPlace: String,
    val returningDate: Date,
    val returningPlace: String
)

class Service(
    val client: Client,
    val firstVisit: Date,
    val lastVisit: Date,
    val totalVisits: Int,
    val notes: String,
    val payment: Payment,
    val priceTotal: Int,
    val keys: Keys
)

