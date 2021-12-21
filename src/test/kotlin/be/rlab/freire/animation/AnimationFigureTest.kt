package be.rlab.freire.animation

import be.rlab.freire.animation.Rect.Companion.rect
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class AnimationFigureTest {
    @Test
    fun configure() {
        val walk1 = " [┐∵]┘ "
        val walk2 = " [┘∵]┐ "
        val robot = AnimationFigure.figure("Robot", rect(0, 0, 45, 16)) {
            sprite(defaultAction = "walk", defaultDirection = Direction.RIGHT) {
                draw("walk", Direction.RIGHT) { walk1 }
                draw("walk", Direction.RIGHT) { walk2 }
                draw("walk", Direction.LEFT, flip(walk1, Orientation.HORIZONTAL))
                draw("walk", Direction.LEFT, flip(walk2, Orientation.HORIZONTAL))
            }
            on {
                every(1000, TimeUnit.MILLISECONDS) {
                    draw(sprite.next())
                }
            }
        }
//        robot.draw(figure, TextColors.blue)
//        robot.prettyPrint()
    }
}
