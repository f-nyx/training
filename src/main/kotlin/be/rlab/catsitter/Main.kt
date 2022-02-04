import be.rlab.tehanu.Tehanu
import be.rlab.tehanu.annotations.Handler
import be.rlab.tehanu.clients.UpdateContext
import be.rlab.tehanu.clients.telegram.telegram
import java.util.*

/** Entities relationships.
 *
 * one-to-one: an entity has a relation to only one other entity. E.g.: Client to Keys
 *      -> Client class has a "keys" property of type Keys
 * one-to-many: an entity has a relation to one or more objects of another entity. E.g.: Client to Cat(s)
 *      -> Client class has a "cats" property of type List<Cat>
 * many-to-one: a collection of entities has a relation to a single entity. E.g.: Cars to Owner
 * many-to-many: one or more entities have a relation to another collection of entities. E.g.: Houses to Owners
 */


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



