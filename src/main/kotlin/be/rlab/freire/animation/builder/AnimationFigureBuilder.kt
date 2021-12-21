package be.rlab.freire.animation.builder

import be.rlab.freire.animation.*
import java.util.*

class AnimationFigureBuilder(
    private val name: String,
    private val rect: Rect
) {
    companion object {
        /** Default turn length, in milliseconds. */
        private const val DEFAULT_TURN_INTERVAL: Int = 500
        const val DEFAULT_ACTION: String = "stay"
        val DEFAULT_DIRECTION: Direction = Direction.RIGHT
    }

    private lateinit var sprite: Sprite
    private val eventsBuilder = EventsBuilder()
    /** Turn interval, in milliseconds. */
    var turnInterval: Int = DEFAULT_TURN_INTERVAL

    fun sprite(
        defaultAction: String,
        defaultDirection: Direction,
        callback: Sprite.() -> Unit
    ): AnimationFigureBuilder = apply {
        val newSprite = Sprite.new(defaultAction, defaultDirection, rect.width, rect.height, rect.layer)
        callback(newSprite)
        sprite = newSprite
    }

    fun draw(callback: () -> String) {
        sprite = Sprite.new(DEFAULT_ACTION, DEFAULT_DIRECTION, rect.width, rect.height, rect.layer)
        sprite.draw(DEFAULT_ACTION, DEFAULT_DIRECTION) { callback() }
    }

    fun on(callback: EventsBuilder.() -> Unit): AnimationFigureBuilder = apply {
        callback(eventsBuilder)
    }

    fun build(): AnimationFigure {
        return AnimationFigure(
            id = UUID.randomUUID(),
            name,
            Viewport.new(rect),
            sprite,
            turnInterval,
            eventsBuilder.build()
        )
    }
}