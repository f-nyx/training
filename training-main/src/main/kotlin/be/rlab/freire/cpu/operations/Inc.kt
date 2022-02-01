package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class Inc(
    private val processor: Processor,
    private val target: Any,
    private val amount: Long
) : Op {
    override fun exec() {
        val value: Any = requireNotNull(processor.read(target)) { "no value found in memory for target: $target" }
        require(value is Number) { "you can increment only numeric values" }
        processor.write(target, value.toLong() + amount)
    }

    override fun toString(): String = "INC ${mkString(target)}"
}
