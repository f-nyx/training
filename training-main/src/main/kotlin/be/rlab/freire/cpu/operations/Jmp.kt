package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.MemoryRef
import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class Jmp(
    private val processor: Processor,
    private val ref: MemoryRef,
    private val jmpType: JmpOpType
) : Op {
    @Suppress("UNCHECKED_CAST")
    override fun exec() {
        val proc: Any = requireNotNull(processor.memory.read(ref)) { "there is no data at memory address: $ref" }
        try {
            val evaluate: () -> Boolean = when (jmpType) {
                JmpOpType.JNE, JmpOpType.JNZ -> {{ processor.zf == 0 }}
                JmpOpType.JE, JmpOpType.JZ -> {{ processor.zf != 0 }}
                JmpOpType.JMP -> {{ true }}
            }
            if (evaluate()) {
                (proc as () -> Unit).invoke()
            }
        } catch (cause: ClassCastException) {
            throw RuntimeException("the data at memory address '$ref' is not a procedure")
        }
    }

    override fun toString(): String = "$jmpType $ref"
}
