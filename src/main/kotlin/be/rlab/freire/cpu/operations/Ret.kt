package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class Ret(
    private val processor: Processor,
    private val value: Any?
) : Op {
    override fun exec() {
        processor.write(processor.res, processor.read(value))
    }

    override fun toString(): String = "RET ${processor.read(value)}"
}
