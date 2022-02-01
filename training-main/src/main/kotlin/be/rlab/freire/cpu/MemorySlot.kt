package be.rlab.freire.cpu

data class MemorySlot(
    val address: Long,
    var label: String?,
    var value: Any?
) {
    companion object {
        fun new(
            address: Long,
            label: String? = null
        ): MemorySlot = MemorySlot(
            address,
            label,
            value = null
        )
    }
}
