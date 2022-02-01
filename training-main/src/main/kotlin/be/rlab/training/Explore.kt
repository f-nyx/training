package be.rlab.training

import be.rlab.freire.animation.*
import be.rlab.freire.animation.AnimationFigure.Companion.figure
import be.rlab.freire.animation.Rect.Companion.rect
import kotlinx.coroutines.runBlocking

class Game {
    private val robotLeft: String = " └[∵┌] "
    private val scene = Scene.new(screen = DebugScreen(80, 20), rect(0, 0, 80, 20))

    val robot: AnimationFigure = figure("Robot", rect(
        top = 1,
        left = 10,
        width = 7,
        height = 1,
        velocity = Vector2.new(0.0, 0.0)
    )) {
        draw { robotLeft }
    }.apply { draw(sprite.next()) }

    fun run() {
        scene.register(robot)
        scene.play()
    }
}

fun game(callback: Game.() -> Unit) = runBlocking {
    val game = Game()
    callback(game)
    game.run()
}

fun main() = game {
    robot.moveDown()
    robot.moveRight(4)
    robot.moveDown()
//        robot.hit()
}
