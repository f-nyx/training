package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class ArithmeticOp(
    private val processor: Processor,
    private val operand1: Any,
    private val operand2: Any,
    private val type: ArithmeticOpType
) : Op {
    override fun exec() {
        val value1 = processor.read(operand1)
        val value2 = processor.read(operand2)

        require(value1 is Number && value2 is Number) { "Operands must be numbers, found: $value1, $value2" }

        when (type) {
            ArithmeticOpType.ADD -> processor.write(processor.res, value1.toLong() + value2.toLong())
            ArithmeticOpType.SUB -> {
                processor.write(processor.res, value1.toLong() - value2.toLong())
                processor.setZeroFlag(processor.res.value as Number)
            }
            ArithmeticOpType.DIV -> {
                val res: Long = value1.toLong() / value2.toLong()
                processor.write(processor.rem, value1.toLong() % value2.toLong())
                processor.write(processor.res, res)
                processor.setZeroFlag(processor.rem.value as Long)
            }
            ArithmeticOpType.MUL -> value1.toLong() * value2.toLong()
        }
    }

    override fun toString(): String = "$type ${mkString(operand1)}, ${mkString(operand2)}"
}
