package be.rlab.freire.cpu

data class MemoryRef(
    val address: Long?,
    val label: String?
) {
    companion object {
        fun byAddress(address: Long): MemoryRef = MemoryRef(
            address,
            label = null
        )

        fun byLabel(label: String): MemoryRef = MemoryRef(
            address = null,
            label
        )
    }

    override fun toString(): String = when {
        label != null && address != null -> ":$label(0x${address.toString(16)})"
        label != null -> ":$label"
        address != null -> "0x${address.toString(16)}"
        else -> "Invalid Memory Reference"
    }
}
