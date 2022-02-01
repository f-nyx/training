package be.rlab.freire.animation

import be.rlab.freire.animation.support.MetricsUtils.indexToPoint
import be.rlab.freire.animation.support.MetricsUtils.pointToIndex
import com.github.ajalt.mordant.rendering.TextColors

data class Cell(
    var content: Char,
    val point: Point,
    var color: TextColors,
    var layer: Int
) {
    companion object {
        const val NO_CONTENT: Char = ' '

        fun new(
            content: Char,
            point: Point,
            color: TextColors = TextColors.gray,
            layer: Int
        ): Cell = Cell(
            content,
            point,
            color,
            layer
        )

        fun blank(
            point: Point,
            layer: Int
        ): Cell = Cell(
            content = NO_CONTENT,
            point,
            color = TextColors.gray,
            layer
        )

        fun frame(
            height: Int,
            width: Int,
            layer: Int
        ): List<Cell> {
            val cells: MutableList<Cell> = mutableListOf()
            val size: Int = sizeOfMatrix(height, width)
            for (i in 0 until size) {
                val point = indexToPoint(index = i, width = width)
                cells += new(NO_CONTENT, point, TextColors.gray, layer)
            }
            return cells
        }

        fun frameFrom(
            content: String,
            height: Int,
            width: Int,
            layer: Int = 0,
            color: TextColors = TextColors.gray
        ): List<Cell> {
            val cells: List<Cell> = frame(height = height, width = width, layer = layer)
            val rows = content.split("\n").map { row ->
                if (row.length < width) {
                    row.padStart(width - row.length / 2, NO_CONTENT)
                    row.padEnd(width - row.length / 2, NO_CONTENT)
                } else {
                    row
                }
            }
            require(rows.size <= height) { "you cannot add a figure higher than the specified height" }
            require(rows.all { row -> row.length <= width }) {
                "you cannot add a figure wider than the specified width"
            }
            rows.forEachIndexed { rowIndex, row ->
                for (column in row.indices) {
                    val pointIndex: Int = pointToIndex(Point.new(column, rowIndex), width)
                    cells[pointIndex].update(row[column], color)
                }
            }
            return cells
        }

        fun toString(
            figure: List<Cell>,
            width: Int
        ): String {
            return figure.chunked(width).joinToString("\n") { row ->
                row.joinToString("") { cell -> cell.content.toString() }
            }
        }

        fun sizeOfMatrix(
            height: Int,
            width: Int
        ): Int {
            return height * width
        }
    }

    private var dirty: Boolean = true

    fun clear(): Cell = apply {
        if (content != NO_CONTENT && color != TextColors.gray) {
            dirty = true
        }
        content = NO_CONTENT
        color = TextColors.gray
    }

    fun update(
        newContent: Char,
        newColor: TextColors = TextColors.black
    ): Cell = apply {
        if (content != newContent || color != newColor) {
            dirty = true
        }
        content = newContent
        color = newColor
    }

    fun isEmpty(): Boolean =
        content == NO_CONTENT && color == TextColors.gray

    fun isNotEmpty(): Boolean = !isEmpty()

    fun samePoint(cell: Cell): Boolean {
        return cell.point == point
    }

    fun sameCell(cell: Cell): Boolean {
        return samePoint(cell)
            && cell.content == content
            && cell.color == color
    }

    fun isDirty(): Boolean = dirty

    fun clean(): Cell = apply {
        dirty = false
    }
}
