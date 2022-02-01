package be.rlab.freire.animation

import be.rlab.freire.animation.Cell.Companion.frameFrom
import be.rlab.freire.animation.Rect.Companion.rect
import be.rlab.freire.animation.support.SceneUtils
import com.github.ajalt.mordant.rendering.TextColors
import java.util.*
import kotlin.math.max

data class Viewport(
    val id: UUID,
    val cells: MutableList<Cell>,
    val rect: Rect
) {
    companion object {
        fun new(rect: Rect): Viewport = Viewport(
            id = UUID.randomUUID(),
            cells = Cell.frame(rect.height, rect.width, rect.layer).toMutableList(),
            rect = rect
        )
    }

    private val regions: MutableList<Region> = mutableListOf()

    fun update(frame: Frame) {
        rect.update(frame)
    }

    /** Renders this viewport and all the mapped regions.
     */
    fun render(): Viewport = apply {
        regions.forEach { region ->
            (findCollisions(region) + region)
                .sortedBy { it.source.rect.layer }
                .forEach {
                    it.markAsDirty()
                    it.render()
                }
        }
    }

    /** Maps another Viewport into this Viewport.
     * The source Viewport must be intersecting this Viewport.
     *
     * @param source Viewport to map into this viewport.
     * @return a Viewport that contains every cell in the intersection.
     */
    fun map(source: Viewport): Region {
        val regionRect = rect.intersection(source.rect)
        val sourceTop: Int = max(0, source.rect.height - (source.rect.bottom - rect.top))
        val sourceLeft: Int = max(0, source.rect.width - (source.rect.right - rect.left))
        val sourceCells: List<Cell> = SceneUtils.extractRegion(
            matrix = source.cells,
            width = source.rect.width,
            rect = rect(sourceTop, sourceLeft, regionRect.width, regionRect.height, regionRect.layer)
        )
        val targetCells: List<Cell> = (0 until regionRect.area).map { index ->
            val row = regionRect.top + (index / regionRect.width)
            val column = regionRect.left + (index % regionRect.width)
            cells[row * rect.width + column]
        }

        return Region.new(
            source = source.translate(regionRect, sourceCells),
            target = translate(regionRect, targetCells)
        ).apply {
            regions += this
        }
    }

    /** Returns a copy of this viewport with all the cells cleaned.
     * @return a copy of the viewport with clean cells.
     */
    fun blank(): Viewport = copy(
        rect = rect.copy(),
        cells = cells.map { cell -> Cell.blank(cell.point, cell.layer) }.toMutableList()
    )

    fun draw(
        content: String,
        color: TextColors = TextColors.black
    ): Viewport = apply {
        draw(frameFrom(content, rect.height, rect.width, rect.layer, color))
    }

    fun draw(content: List<Cell>): Viewport = apply {
        require(content.size == cells.size) { "Content must have the same dimensions than this viewport" }
        content.forEachIndexed { index, newCell -> cells[index].update(newCell.content, newCell.color) }
    }

    /** Copies this viewport to another viewport with a different rectangle.
     * @param newRect New viewport rectangle.
     * @param cells Cells for the new viewport.
     */
    fun translate(
        newRect: Rect,
        cells: List<Cell>
    ): Viewport {
        require(cells.size == newRect.area) {
            "Invalid matrix, it must contain the same number of points than the specified rect"
        }
        return copy(
            rect = rect
                .moveTo(newRect.position)
                .resize(newRect.height, newRect.width),
            cells = cells.toMutableList()
        )
    }

    /** Verifies if any part of another viewport is within the boundaries of this viewport using the AABB algorithm.
     *
     * @param viewport Viewport to verify.
     * @return true if the provided viewport is within the boundaries of this viewport, false otherwise.
     */
    operator fun contains(viewport: Viewport): Boolean {
        return viewport.rect in rect
    }

    /** Verifies if there is any collision between this viewport and another viewport.
     *
     * A collision happens if any edge is within the coordinates of the other viewport and
     * there is overlapping between at least one cell.
     *
     * @param viewport Viewport to check for collisions.
     * @return true if there is any collision between the two viewports.
     */
    fun collision(viewport: Viewport): Boolean {
        return viewport in this && cells.any { cell1 ->
            cell1.isNotEmpty() && viewport.cells.any { cell2 ->
                cell2.isNotEmpty() && cell1.samePoint(cell2)
            }
        }
    }

    fun clean() {
        rect.clean()
        cells.forEach(Cell::clean)
    }

    fun isDirty(): Boolean {
        return rect.isDirty() || cells.any(Cell::isDirty)
    }

    private fun findCollisions(region: Region): List<Region> {
        return regions.filter { otherRegion ->
            otherRegion != region && region.target.collision(otherRegion.target)
        }
    }
}
