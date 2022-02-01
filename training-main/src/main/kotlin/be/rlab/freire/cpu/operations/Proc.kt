package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.Memory
import be.rlab.freire.cpu.MemoryRef
import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Processor

class Proc(
    processor: Processor,
    private val name: String,
    private val callback: () -> Unit
) : Op {
    companion object {
        private const val PROC_COUNT_OFFSET: Long = 0x8000
        const val PROC_OFFSET: Long = PROC_COUNT_OFFSET + 1
        private const val COUNT_SLOT_NAME: String = "proc_count"
        private val COUNT_SLOT = MemoryRef(PROC_COUNT_OFFSET, COUNT_SLOT_NAME)
    }
    private val memory: Memory = processor.memory

    override fun exec() {
        val count: Int = memory.read(COUNT_SLOT)?.let { it as Int } ?: -1
        val nextOffset: Int = count + 1
        memory.alloc(COUNT_SLOT)
        memory.write(COUNT_SLOT, nextOffset)

        val procRef = MemoryRef(PROC_OFFSET + nextOffset, name)
        memory.alloc(procRef)
        memory.write(procRef, callback)
    }

    override fun toString(): String = "PROC $name"
}
