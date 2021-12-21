package be.rlab.freire.animation

data class Placement(
    val top: Int,
    val left: Int
) {
    companion object {
        fun new(
            row: Int,
            column: Int
        ): Placement = Placement(
            row,
            column
        )
    }
}
