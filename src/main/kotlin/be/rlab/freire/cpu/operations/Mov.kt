package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class Mov(
    private val processor: Processor,
    internal val target: Any,
    internal val source: Any?
) : Op {
    override fun exec() {
        processor.write(target, source)
    }

    override fun toString(): String = "MOV ${mkString(target)}, ${mkString(source)}"
}
