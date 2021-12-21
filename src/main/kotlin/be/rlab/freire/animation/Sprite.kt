package be.rlab.freire.animation

import com.github.ajalt.mordant.rendering.TextColors
import java.io.InputStream
import java.util.*

class Sprite(
    private var action: String,
    private var direction: Direction,
    private val width: Int,
    private val height: Int,
    private val layer: Int
) {
    companion object {
        fun new(
            defaultAction: String,
            defaultDirection: Direction,
            width: Int,
            height: Int,
            layer: Int
        ): Sprite = Sprite(
            action = defaultAction,
            direction = defaultDirection,
            width,
            height,
            layer
        )
    }

    private data class SpriteFigure(
        val id: UUID,
        val action: String,
        val direction: Direction,
        val cells: List<Cell>
    ) {
        companion object {
            fun new(
                action: String,
                direction: Direction,
                cells: List<Cell>
            ): SpriteFigure = SpriteFigure(
                id = UUID.randomUUID(),
                action,
                direction,
                cells
            )
        }
    }

    private val figures: MutableList<SpriteFigure> = mutableListOf()
    private var lastFigureIndex: Int = 0

    fun next(): List<Cell> {
        val currentFigures: List<SpriteFigure> = figures.filter { figure ->
            figure.action == action && figure.direction == direction
        }
        require(currentFigures.isNotEmpty()) { "no figure defined for action $action and direction $direction" }

        if (lastFigureIndex >= currentFigures.size) {
            lastFigureIndex = 0
        }
        return currentFigures[lastFigureIndex++].cells
    }

    fun move(newDirection: Direction): Sprite = apply {
        val changed: Boolean = newDirection != direction
        direction = newDirection

        if (changed) {
            lastFigureIndex = 0
        }
    }

    fun act(
        newAction: String,
        newDirection: Direction = direction
    ): Sprite = apply {
        val changed: Boolean = newAction != action || newDirection != direction

        action = newAction
        direction = newDirection

        if (changed) {
            lastFigureIndex = 0
        }
    }

    fun draw(
        action: String,
        direction: Direction,
        figure: List<Cell>
    ): Sprite = apply {
        figures += SpriteFigure.new(action, direction, figure)
    }

    fun draw(
        action: String,
        direction: Direction,
        color: TextColors = TextColors.black,
        figure: InputStream
    ): Sprite = draw(action, direction, color, figure.bufferedReader()::readText)

    fun draw(
        action: String,
        direction: Direction,
        color: TextColors = TextColors.black,
        figure: () -> String
    ): Sprite = apply {
        val cells: List<Cell> = Cell.frameFrom(
            content = figure(),
            height = height,
            width = width,
            layer = layer,
            color = color
        )
        figures += SpriteFigure.new(action, direction, cells)
    }

    fun flip(
        figure: String,
        orientation: Orientation
    ): List<Cell> {
        return Transform.flip(figure, height, width, orientation)
    }

    fun flip(
        figure: List<Cell>,
        orientation: Orientation
    ): List<Cell> {
        return Transform.flip(figure, height, width, orientation)
    }
}
