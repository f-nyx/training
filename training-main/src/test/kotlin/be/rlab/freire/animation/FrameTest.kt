package be.rlab.freire.animation

import be.rlab.freire.animation.Rect.Companion.rect
import org.junit.jupiter.api.Test

class FrameTest {
    private val robotLeft: String = " └[∵┌] "
    private val robotFront: String = "└[ ∵ ]┘"
    private val robotRight: String = " [┐∵]┘ "
    private val character: String = Thread.currentThread().contextClassLoader
        .getResourceAsStream("ascii/character.txt")?.bufferedReader()?.readText()
        ?: throw RuntimeException("resource not found")

    @Test
    fun render() {
        val robot1 = AnimationFigure.figure("Robot1", rect(10, 10, 7, 1)).draw(robotLeft)
        val robot2 = AnimationFigure.figure("Robot2", rect(9, 10, 7, 1)).draw(robotRight)
        val robot3 = AnimationFigure.figure("Robot3", rect(0, 6, 7, 1)).draw(robotFront)
        val cat = AnimationFigure.figure("Cat", rect(0, 0, 8, 4)).draw(character)
        val frame = Frame.new(
            rect(0, 0, 20, 20),
            figures = listOf(robot1, robot2, cat, robot3),
            deltaTime = 0.0
        )
        frame.render()
    }
}
