package be.rlab.freire.animation

import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

class LifeCycleEvents {
    companion object {
        const val START: String = "start"
        const val FRAME_INTERVAL: String = "tick"
        const val TURN_INTERVAL: String = "turn-interval"
        const val TIME_INTERVAL: String = "time-interval"
        const val COLLISION: String = "collision"
    }

    private abstract class EventHandler(val eventName: String) {
        abstract fun handle(vararg data: Any)
    }

    private class SceneHandler(
        eventName: String,
        private val callback: (Scene) -> Unit
    ): EventHandler(eventName) {
        override fun handle(vararg data: Any) {
            require(data.size == 1 && data[0] is Scene)
            callback(data[0] as Scene)
        }
    }

    private class FrameHandler(
        eventName: String,
        private val callback: AnimationFigure.(Frame) -> Unit
    ): EventHandler(eventName) {
        override fun handle(vararg data: Any) {
            require(data.size == 2 && data[0] is AnimationFigure && data[1] is Frame)
            callback(data[0] as AnimationFigure, data[1] as Frame)
        }
    }

    private class TimeIntervalHandler(
        val intervalInMillis: Long,
        var lastTick: Long,
        val callback: AnimationFigure.(Frame) -> Unit
    ) : EventHandler(TIME_INTERVAL) {
        override fun handle(vararg data: Any) {
            require(data.size == 2 && data[0] is AnimationFigure && data[1] is Frame)
            val elapsedTime = Duration.between(
                Instant.ofEpochMilli(lastTick),
                Instant.ofEpochMilli(System.currentTimeMillis())
            )

            if (elapsedTime.toMillis() > intervalInMillis) {
                callback(data[0] as AnimationFigure, data[1] as Frame)
                lastTick = System.currentTimeMillis()
            }
        }
    }

    private val handlers: MutableList<EventHandler> = mutableListOf()

    fun start(callback: (Scene) -> Unit): LifeCycleEvents = apply {
        handlers += SceneHandler(START, callback)
    }

    fun tick(callback: AnimationFigure.(Frame) -> Unit): LifeCycleEvents = apply {
        handlers += FrameHandler(FRAME_INTERVAL, callback)
    }

    fun turn(callback: AnimationFigure.(Frame) -> Unit): LifeCycleEvents = apply {
        handlers += FrameHandler(TURN_INTERVAL, callback)
    }

    fun every(
        interval: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
        callback: AnimationFigure.(Frame) -> Unit
    ): LifeCycleEvents = apply {
        handlers += TimeIntervalHandler(
            intervalInMillis = TimeUnit.MILLISECONDS.convert(interval, unit),
            lastTick = System.currentTimeMillis(),
            callback
        )
    }

    fun trigger(
        eventName: String,
        vararg data: Any
    ): LifeCycleEvents = apply {
        handlers.filter { handler -> handler.eventName == eventName }.forEach { handler ->
            handler.handle(*data)
        }
    }
}
