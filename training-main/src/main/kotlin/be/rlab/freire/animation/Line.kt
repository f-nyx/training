package be.rlab.freire.animation

data class Line(
    val origin: Vector2,
    val destination: Vector2
) {

    companion object {
        fun new(
            origin: Vector2,
            destination: Vector2
        ): Line = Line(origin, destination)
    }

    val segment: Vector2 = destination - origin

    /** Determines whether two lines intersect each other.
     * It uses the line-segment interception algorithm.
     */
    fun intersects(line: Line): Boolean {
        val crossProduct: Double = segment x line.segment
        val t = ((line.origin - origin) x line.segment) / crossProduct
        val u = ((line.origin - origin) x segment) / crossProduct
        return u in 0.0..1.0 && t in 0.0..1.0
    }
}
