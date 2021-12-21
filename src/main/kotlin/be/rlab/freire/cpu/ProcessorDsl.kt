package be.rlab.freire.cpu

/** Program _entry point_.
 *
 * Use this instruction to start a program. Write your program between the braces:
 *
 * ```
 * fun main() = entryPoint {
 *   // Your program goes here.
 * }
 * ```
 */
fun entryPoint(callback: Processor.() -> Unit): Processor {
    val processor = Processor()
    callback(processor)
    if (!processor.end) {
        throw RuntimeException("program didn't call the end() instruction")
    }
    return processor
}
