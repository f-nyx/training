package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class End(private val processor: Processor) : Op {
    override fun exec() {
        processor.end = true
    }

    override fun toString(): String = "END"
}
