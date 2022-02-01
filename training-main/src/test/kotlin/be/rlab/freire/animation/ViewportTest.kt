package be.rlab.freire.animation

import be.rlab.freire.animation.Rect.Companion.rect
import com.github.ajalt.mordant.rendering.TextColors
import org.junit.jupiter.api.Test

class ViewportTest {
    private val figure: String = readResource("ascii/radio.txt")

    @Test
    fun clear() {
        val radio = Viewport.new(rect(0, 0, 45, 16))

        radio.draw(figure, TextColors.blue)
//        radio.clear()
        assert(radio.cells.all(Cell::isEmpty))
    }
}