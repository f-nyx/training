package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.MemoryRef
import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class Alloc(
    private val processor: Processor,
    private val address: Long,
    private val label: String?
) : Op {
    override fun exec() {
        processor.memory.alloc(MemoryRef(address, label))
    }

    override fun toString(): String = "ALLOC ${MemoryRef(address, label)}"
}
