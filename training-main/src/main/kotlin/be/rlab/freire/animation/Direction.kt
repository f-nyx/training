package be.rlab.freire.animation

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT;

    companion object {
        fun of(vector: Vector2): Direction {
            return when {
                vector.x >=0 && vector.y >= 0 -> DOWN_RIGHT
                vector.x > 0 && vector.y < 0 -> UP_RIGHT
                vector.x < 0 && vector.y > 0 -> DOWN_LEFT
                vector.x < 0 && vector.y < 0 -> UP_LEFT
                vector.x >= 0 && vector.y == 0.0 -> RIGHT
                vector.x < 0 && vector.y == 0.0 -> LEFT
                vector.x == 0.0 && vector.y >= 0 -> DOWN
                vector.x == 0.0 && vector.y < 0 -> UP
                else -> throw RuntimeException("Invalid direction $vector")
            }
        }
    }
}
