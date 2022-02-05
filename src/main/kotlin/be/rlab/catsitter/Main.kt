import be.rlab.tehanu.Tehanu
import be.rlab.tehanu.annotations.Handler
import be.rlab.tehanu.clients.UpdateContext
import be.rlab.tehanu.clients.telegram.telegram
import be.rlab.tehanu.messages.model.Message

/** Entities relationships.
 *
 * one-to-one: an entity has a relation to only one other entity. E.g.: Client to Keys
 *      -> Client class has a "keys" property of type Keys
 * one-to-many: an entity has a relation to one or more objects of another entity. E.g.: Client to Cat(s)
 *      -> Client class has a "cats" property of type List<Cat>
 * many-to-one: a collection of entities has a relation to a single entity. E.g.: Cars to Owner
 * many-to-many: one or more entities have a relation to another collection of entities. E.g.: Houses to Owners
 */


// This is the name of the command we'll use in Telegram (like /newbot in BothFather)
// UpdateContext documentation: https://git.rlab.be/seykron/tehanu/-/wikis/home#context
@Handler(name = "/say_hello")
fun sayHello(context: UpdateContext) {
    context.talk("hello!")
}

@Handler(name = "/service_price")
fun calculateServicePrice(
    context: UpdateContext,
    message: Message
) {
    val parameters: List<String> = message.text
        .substringAfter(" ")
        .split(" ")
    val totalVisits: Int = parameters[0].split("=")[1].toInt()
    context.answer(totalVisits.toString())

    // Parse all parameters
    parameters.forEach { parameter: String ->
        val chunks: List<String> = parameter.split("=")
        val name: String = chunks[0]
        val value: String = chunks[1]
        context.talk("$name -> $value")
    }
}

fun main(args: Array<String>) {
    println("starting catsitter bot")

    Tehanu.configure {
        handlers {
            register(::sayHello)
            register(::calculateServicePrice)
        }

        clients {
            telegram(System.getenv("TELEGRAM_TOKEN"))
        }
    }.start()
}
