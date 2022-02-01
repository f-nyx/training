package be.rlab.freire.cpu

data class Register(
    val name: String,
    var value: Any?
) {
    companion object {
        fun new(name: String): Register = Register(
            name,
            value = null
        )
    }
    override fun toString(): String = "$name=$value"
}
