package be.rlab.freire.animation

data class Point(
    val x: Double,
    val y: Double
) {
    companion object {
        fun new(
            x: Int,
            y: Int
        ): Point = Point(
            x = x.toDouble(),
            y = y.toDouble()
        )
    }

    operator fun plus(vector: Vector2): Point {
        return copy(
            x = x + vector.x,
            y = y + vector.y
        )
    }

    fun moveX(distance: Double): Point {
        return this + Vector2.new(distance, 0.0)
    }

    fun moveY(distance: Double): Point {
        return this + Vector2.new(0.0, distance)
    }

    fun translate(to: Point): Vector2 = Vector2.new(
        x = to.x - x,
        y = to.y - y
    )
}
