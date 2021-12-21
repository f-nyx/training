package be.rlab.freire.animation.builder

import be.rlab.freire.animation.AnimationFigure
import be.rlab.freire.animation.Frame
import be.rlab.freire.animation.LifeCycleEvents
import be.rlab.freire.animation.Viewport
import java.util.concurrent.TimeUnit

class EventsBuilder {

    private val events: LifeCycleEvents = LifeCycleEvents()

    fun every(
        interval: Long,
        unit: TimeUnit = TimeUnit.MILLISECONDS,
        callback: AnimationFigure.(Frame) -> Unit
    ): EventsBuilder = apply {
        events.every(interval, unit, callback)
    }

    fun tick(
        callback: AnimationFigure.(Frame) -> Unit
    ): EventsBuilder = apply {
        events.tick(callback)
    }

    fun turn(
        callback: AnimationFigure.(Frame) -> Unit
    ): EventsBuilder = apply {
        events.turn(callback)
    }
//
//    fun collision(callback: (Viewport) -> Unit): EventsBuilder = apply {
//
//    }

    fun build(): LifeCycleEvents {
        return events
    }
}
