package be.rlab.freire.animation

import be.rlab.freire.animation.LifeCycleEvents.Companion.FRAME_INTERVAL
import be.rlab.freire.animation.LifeCycleEvents.Companion.TIME_INTERVAL
import be.rlab.freire.animation.LifeCycleEvents.Companion.TURN_INTERVAL
import be.rlab.freire.animation.builder.AnimationFigureBuilder
import com.github.ajalt.mordant.rendering.TextColors
import java.util.*

class AnimationFigure(
    val id: UUID,
    override val name: String,
    val viewport: Viewport,
    val sprite: Sprite,
    private val turnInterval: Int,
    private val events: LifeCycleEvents
) : SceneComponent() {
    companion object {
        fun figure(
            name: String,
            rect: Rect,
            callback: AnimationFigureBuilder.() -> Unit = {}
        ): AnimationFigure {
            val builder = AnimationFigureBuilder(name, rect)
            callback(builder)
            return builder.build()
        }
    }

    private var deltaTurn: Double = 0.0

    override fun start(scene: Scene) {
        draw(sprite.next())
    }

    /** Reports the next loop iteration.
     */
    override fun update(frame: Frame) {
        deltaTurn += frame.deltaTime

        if (deltaTurn >= turnInterval) {
            events.trigger(TURN_INTERVAL, this, frame)
            viewport.update(frame)
            deltaTurn = 0.0
        }

        events.trigger(FRAME_INTERVAL, this, frame)
        events.trigger(TIME_INTERVAL, this, frame)
    }

    fun distanceTo(figure: AnimationFigure): Double {
        return viewport.rect.distanceTo(figure.viewport.rect)
    }

    fun moveUp(distance: Int = 1): AnimationFigure = move(Direction.UP, distance)
    fun moveDown(distance: Int = 1): AnimationFigure = move(Direction.DOWN, distance)
    fun moveLeft(distance: Int = 1): AnimationFigure = move(Direction.LEFT, distance)
    fun moveRight(distance: Int = 1): AnimationFigure = move(Direction.RIGHT, distance)

    fun draw(
        content: String,
        color: TextColors = TextColors.black
    ): AnimationFigure = apply {
        viewport.draw(content, color)
    }

    fun draw(content: List<Cell>): AnimationFigure = apply {
        viewport.draw(content)
    }

    private fun move(
        direction: Direction,
        distance: Int
    ): AnimationFigure = apply {
//        when (direction) {
//            Direction.UP -> viewport.top += distance
//            Direction.DOWN -> viewport.top -= distance
//            Direction.LEFT -> viewport.left -= distance
//            Direction.RIGHT -> viewport.left += distance
//        }
//        this.direction = direction
    }
}
