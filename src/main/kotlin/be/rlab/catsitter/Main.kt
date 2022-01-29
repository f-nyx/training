import be.rlab.tehanu.Tehanu
import be.rlab.tehanu.annotations.Handler
import be.rlab.tehanu.clients.UpdateContext
import be.rlab.tehanu.clients.telegram.telegram

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
