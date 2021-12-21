package be.rlab.freire.cpu

class Memory {
    companion object {
        private const val DATA_OFFSET: Long = 0x1000
    }
    private val slots: MutableMap<Long, MemorySlot> = mutableMapOf()

    fun alloc(ref: MemoryRef) {
        resolve(ref)?.let { slot ->
            slot.label = ref.label
            slot.value = null
        } ?: let {
            val address: Long = resolveAddress(ref.address)
            slots.putIfAbsent(address, MemorySlot.new(address, ref.label))
        }
    }

    fun write(
        ref: MemoryRef,
        value: Any?
    ): MemorySlot {
        val slot: MemorySlot = resolve(ref) ?: throw RuntimeException("memory not allocated: $ref")
        slot.value = value
        return slot
    }

    fun read(ref: MemoryRef): Any? {
        return resolve(ref)?.value
    }

    fun has(ref: MemoryRef): Boolean {
        return resolve(ref) != null
    }

    private fun resolve(ref: MemoryRef): MemorySlot? {
        return when {
            ref.label != null -> slots.values.find { slot -> slot.label == ref.label }
            ref.address != null -> slots[ref.address]
            else -> throw RuntimeException("invalid memory reference, either address or label must be provided")
        }
    }

    private fun resolveAddress(address: Long?): Long {
        require(address == null || address >= DATA_OFFSET) {
            "The memory addresses from 0x0 to 0x999 are reserved for the program. " +
            "You can alloc memory from address 0x1000"
        }
        return address ?: nextFreeAddress()
    }

    private fun nextFreeAddress(): Long {
        if (slots.isEmpty()) {
            return DATA_OFFSET
        }
        var slot = slots[DATA_OFFSET]
        var offset = 0

        while (slot != null) {
            offset += 1
            slot = slots[DATA_OFFSET + offset]
        }

        return DATA_OFFSET + offset
    }
}