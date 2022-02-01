package be.rlab.freire.animation

fun readResource(path: String): String {
    return Thread.currentThread().contextClassLoader
        .getResourceAsStream(path)?.bufferedReader()?.readText()
        ?: throw RuntimeException("resource not found")
}