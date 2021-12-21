package be.rlab.freire.animation

import be.rlab.freire.animation.AnimationFigure.Companion.figure
import be.rlab.freire.animation.Rect.Companion.rect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class SceneTest {
    private val robotLeft: String = " └[∵┌] "
    private val robotFront: String = "└[ ∵ ]┘"
    private val robotRight: String = " [┐∵]┘ "
    private val character: String = Thread.currentThread().contextClassLoader
        .getResourceAsStream("ascii/character.txt")?.bufferedReader()?.readText()
        ?: throw RuntimeException("resource not found")

    @Test
    fun render() = runBlocking {
        val ACTION = "walk"
        val robotT = figure("RobotT", rect(top = 4, left = 0, height = 1, width = 7, velocity = Vector2.new(0.5, 0.0))) {
            sprite(ACTION, Direction.RIGHT) {
                val walk1 = " [┐∵]┘ "
                val walk2 = " [┘∵]┐ "

                draw(action = ACTION, Direction.RIGHT) { walk1 }
                draw(action = ACTION, Direction.RIGHT) { walk2 }
                draw(action = ACTION, Direction.LEFT, flip(walk1, Orientation.HORIZONTAL))
                draw(action = ACTION, Direction.LEFT, flip(walk2, Orientation.HORIZONTAL))
            }
//            script {
//
//            }
            on {
                turn {
                    draw(sprite.next())
                }
            }
        }
        val robotX = figure("RobotX", rect(top = 4, left = 20, height = 1, width = 7, velocity = Vector2.new(-0.5, 0.2))) {
            sprite(ACTION, Direction.LEFT) {
                val walk1 = " [┐∵]┘ "
                val walk2 = " [┘∵]┐ "

                draw(action = ACTION, Direction.RIGHT) { walk1 }
                draw(action = ACTION, Direction.RIGHT) { walk2 }
                draw(action = ACTION, Direction.LEFT, flip(walk1, Orientation.HORIZONTAL))
                draw(action = ACTION, Direction.LEFT, flip(walk2, Orientation.HORIZONTAL))
            }
//            script {
//
//            }
            on {
                turn { frame ->
                    draw(sprite.next())
                    val target = frame.findFigureByName("RobotT")!!
                    val distance = distanceTo(target)
                    println(
                        "$distance collision: ${viewport.collision(target.viewport)} " +
                        "adjacent: ${viewport.rect.adjacent(target.viewport.rect)} " +
                        "direction: ${viewport.rect.direction}"
                    )
                }
            }
        }

        val robot1 = figure("Robot1", rect(top = 1, left = 10, width = 7, height = 1)) {
            draw { robotLeft }
        }
        val robot2 = figure("Robot2", rect(top = 2, left = 10, width = 7, height = 1)) {
            draw { robotRight }
        }
//        val robot3 = figure("Robot3", rect(top = 9, left = 6, height = 1, width = 7)) {
//            draw { robotFront }
//        }.apply { draw(sprite.next()) }
        val robot3 = figure("Robot3", rect(top = 9, left = 6, width = 7, height = 1, layer = 1)) {
            sprite(ACTION, Direction.RIGHT) {
                val stay1 = "└[ ∵ ]┘"
                val stay2 = "└[ ∵ ]┐"
                val stay3 = "┌[ ∵ ]┐"
                val stay4 = "┌[ ∵ ]┘"

                draw(action = ACTION, Direction.RIGHT) { stay1 }
                draw(action = ACTION, Direction.RIGHT) { stay2 }
                draw(action = ACTION, Direction.RIGHT) { stay3 }
                draw(action = ACTION, Direction.RIGHT) { stay4 }
            }
            on {
                turn {
                    draw(sprite.next())
                }
            }
        }

        val cat = figure("Cat", rect(top = 6, left = 0, width = 8, height = 4, layer = -1, velocity = Vector2.new(1.0, 0.0))) {
            draw { character }
        }
        val scene = Scene.new(screen = DebugScreen(80, 20), rect(0, 0, 80, 20))
        scene.register(robotT)
        scene.register(robotX)
        scene.register(robot1)
        scene.register(robot2)
        scene.register(cat)
        scene.register(robot3)

//        val r1 = rect(0, 0, 10, 10, rotation = Math.toRadians(0.0))
//        val r2 = rect(5, 5, 10, 10, rotation = Math.toRadians(0.0))
//        println(r1.collision(r2))
        val p = Vector2.new(-2.0, 3.0).projection(Vector2.new(6.0, 0.0))
        println(p)
        val intersects = Line.new(
            origin = Vector2.new(0.0, 0.0),
            destination = Vector2.new(3.0, 3.0)
        ).intersects(Line.new(
            origin = Vector2.new(3.0, 0.0),
            destination = Vector2.new(0.0, 3.0)
        ))
        println(intersects)
        scene.play()
    }
}
