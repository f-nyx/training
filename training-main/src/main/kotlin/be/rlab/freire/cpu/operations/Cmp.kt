package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class Cmp(
    private val processor: Processor,
    private val operand1: Any,
    private val operand2: Any
) : Op {
    @Suppress("UNCHECKED_CAST")
    override fun exec() {
        val value1: Any = requireNotNull(processor.read(operand1)) { "no value found in memory for operand: $operand1" }
        val value2: Any = requireNotNull(processor.read(operand2)) { "no value found in memory for operand: $operand2" }
        val resolvedValue1 = when(value1) {
            is Number -> value1.toLong()
            else -> value1
        }
        val resolvedValue2 = when(value2) {
            is Number -> value2.toLong()
            else -> value2
        }

        processor.setZeroFlag(
            if (resolvedValue1 is Comparable<*> && resolvedValue2 is Comparable<*>) {
                (resolvedValue2 as Comparable<Any>).compareTo(resolvedValue1)
            } else {
                throw RuntimeException("cannot compare operands: $operand1, $operand2")
            }
        )
    }

    override fun toString(): String = "CMP ${mkString(operand1)}, ${mkString(operand2)}"
}
