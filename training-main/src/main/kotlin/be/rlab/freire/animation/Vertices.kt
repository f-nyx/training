package be.rlab.freire.animation

/** Contains the vectors that represent the four corners of a [Rect].
 */
data class Vertices(
    val topLeft: Vector2,
    val topRight: Vector2,
    val bottomRight: Vector2,
    val bottomLeft: Vector2
) {
    companion object {
        fun new(rect: Rect): Vertices {
            val corners = listOf(
                Vector2.new(rect.left.toDouble(), rect.top.toDouble()),
                Vector2.new(rect.left.toDouble(), rect.bottom.toDouble()),
                Vector2.new(rect.right.toDouble(), rect.top.toDouble()),
                Vector2.new(rect.right.toDouble(), rect.bottom.toDouble())
            ).map { vertexInWorldCoords ->
                val vertex = (vertexInWorldCoords - rect.center).rotate(rect.rotation)
                rect.center + vertex
            }
            return Vertices(
                topLeft = corners[0],
                topRight = corners[2],
                bottomRight = corners[3],
                bottomLeft = corners[1]
            )
        }
    }

    val all: List<Vector2> = listOf(topLeft, topRight, bottomRight, bottomLeft)
}
