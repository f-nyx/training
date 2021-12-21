package be.rlab.freire.animation

import org.junit.jupiter.api.Test

class TransformTest {
    private val figure: String = readResource("ascii/radio.txt")
    private val figureFlip: String = readResource("ascii/radio_flip.txt")

    @Test
    fun flip() {
        val transformed = Cell.toString(
            Transform.flip(
                Transform.flip(figure, 16, 45, Orientation.HORIZONTAL),
                16,
                45,
                Orientation.VERTICAL
            )
            , 45
        )
        assert(transformed == figureFlip)
    }
}
