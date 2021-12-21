package be.rlab.freire.animation

import kotlin.math.max
import kotlin.math.min

/** Represents a static rectangle in the [Scene].
 */
data class Rect(
    /** Rectangle absolute position in world coordinates.
     * This position represents the top-left corner of the rectangle.
     */
    var position: Point,
    /** Rectangle width, in [Cell]s. */
    val width: Int,
    /** Rectangle height, in [Cell]s. */
    val height: Int,
    /** Rectangle layer to resolve precedence in case of overlapping. */
    var layer: Int,
    /** Rectangle direction and motion factor. */
    var velocity: Vector2,
    /** Vector that represents a force that goes down in the Y axis. */
    var gravity: Vector2,
    /** Rotation angle, in radians. */
    var rotation: Double,
    /** The padding is used to add a tolerance to collisions. */
    val padding: Int,
    /** Indicates whether this rectangle requires redrawing. */
    private var dirty: Boolean
) {
    companion object {
        fun rect(
            top: Int,
            left: Int,
            width: Int,
            height: Int,
            layer: Int = 0,
            velocity: Vector2 = Vector2.new(0.0, 0.0),
            gravity: Vector2 = Vector2.new(0.0, 0.0),
            rotation: Double = 0.0,
            padding: Int = 1
        ): Rect = Rect(
            position = Point.new(left, top),
            width,
            height,
            layer,
            velocity,
            gravity,
            rotation,
            padding,
            dirty = false
        )
    }

    var top: Int
        get() = position.y.toInt()
        set(value) {
            position = position.moveY(value.toDouble())
        }
    var left: Int
        get() = position.x.toInt()
        set(value) {
            position = position.moveX(value.toDouble())
        }
    val bottom: Int get() = top + height
    val right: Int get() = left + width
    val area: Int get() = height * width
    // TODO(nyx): optimize
    val vertices: Vertices get() = Vertices.new(this)
    val center: Vector2 get () {
        val centerX: Int = left + width / 2
        val centerY: Int = top + height / 2
        return Vector2.new(centerX.toDouble(), centerY.toDouble())
    }
    val direction: Direction get () = Direction.of(velocity)

    /** Moves the rectangle from the previous frame to the next frame position according
     * to the current parameters.
     * @param frame Current frame.
     */
    fun update(frame: Frame) {
        val newPosition = position + velocity
        if (position != newPosition) {
            position = newPosition
            velocity += gravity * frame.deltaTime
            markAsDirty()
        }
    }

    /** Moves this rectangle to an absolute position.
     * @param newPosition Position to move this rectangle to.
     * @return a new rect.
     */
    fun moveTo(newPosition: Point): Rect = copy(
        position = newPosition
    )

    /** Resizes this rectangle.
     * @param newHeight New height, must be greater than 0.
     * @param newWidth New width, must be greater than 0.
     * @return a new rectangle with the new dimensions.
     */
    fun resize(
        newHeight: Int,
        newWidth: Int
    ): Rect = copy(
        width = if (newWidth > 0) {
            newWidth
        } else {
            width
        },
        height = if (newHeight > 0) {
            newHeight
        } else {
            height
        }
    )

    /** Returns a Rect that represents the intersection between this Rect and another Rect.
     * It expects this rect to be intersecting the provided rect.
     * @param rect Rect to calculate the intersection.
     * @return a Rect thar represents the intersection between this rect and the provided rect.
     */
    fun intersection(rect: Rect): Rect {
        require(rect in this) { "There is no intersection between the rects" }

        val regionTop: Int = max(0, max(rect.top, top))
        val regionLeft: Int = max(0, max(rect.left, left))
        val regionHeight = max(0, min(rect.bottom, bottom) - max(rect.top, top))
        val regionWidth = max(0, min(rect.right, right) - max(rect.left, left))

        return copy(
            position = Point.new(regionLeft, regionTop),
            width = regionWidth,
            height = regionHeight
        )
    }

    /** Calculates the distance between the centers of two rectangles.
     */
    fun distanceTo(rect: Rect): Double {
        return center.distanceTo(rect.center)
    }

    /** Verifies if another Rect is adjacent to this rect.
     *
     * Two rects are adjacent if they are connected by an edge. Two edges are adjacent if they share a vertex.
     * This method supports a _tolerance_, the space between two edges that will still be considered adjacent.
     *
     * @param rect Rect to verify.
     * @param tolerance Tolerance, in cells.
     * @return true if the provided rect is adjacent to this rect, false otherwise.
     */
    fun adjacent(
        rect: Rect,
        tolerance: Int = 0
    ): Boolean {
        val rangeX = (rect.left - tolerance)..(rect.right + tolerance)
        val rangeY = (rect.top - tolerance)..(rect.bottom + tolerance)
        if (left !in rangeX && right !in rangeX) {
            return false
        }
        if (top !in rangeY && bottom !in rangeY) {
            return false
        }

        return left == rect.right || right == rect.left || top == rect.bottom || bottom == rect.top
    }

//    // SAT collision detection, WIP
//    fun collision(rect: Rect): Boolean {
//        val sourceCorners = corners
//        val targetCorners = rect.corners
//        val axis = listOf(
//            (sourceCorners.topRight - sourceCorners.topLeft).normalized,
//            (sourceCorners.bottomLeft - sourceCorners.topLeft).normalized,
//            (targetCorners.topRight - targetCorners.topLeft).normalized,
//            (targetCorners.bottomLeft - targetCorners.topLeft).normalized
//        )
//        return axis.all { vector ->
//            val sourceProjections: List<Double> = sourceCorners.all.map { corner -> vector * corner }
//            val targetProjections: List<Double> = targetCorners.all.map { corner -> vector * corner }
//            val sourceMax = max(sourceProjections[0], sourceProjections[1])
//            val sourceMin = min(sourceProjections[2], sourceProjections[3])
//            val targetMax = max(targetProjections[0], targetProjections[1])
//            val targetMin = min(targetProjections[2], targetProjections[3])
//            if (targetMin > sourceMax || targetMax < sourceMin) {
//                false
//            } else {
//                sourceMax > targetMax
//            }
//        }
//    }

    /** Verifies if any part of another Rect is within the boundaries of this rect using the AABB algorithm.
     * @param rect Rect to verify.
     * @return true if the provided rect is within the boundaries of this rect, false otherwise.
     */
    operator fun contains(rect: Rect): Boolean {
        if ((left + padding) > rect.right || (right - padding) < rect.left) {
            return false
        }
        if ((top + padding) > rect.bottom || (bottom - padding) < rect.top) {
            return false
        }

        return true
    }

    /** Indicates whether this rectangle requires redrawing.
     * @return true if it requires redrawing, false otherwise.
     */
    fun isDirty(): Boolean = dirty

    /** Cleans the dirty flag.
     * @return this rectangle.
     */
    fun clean(): Rect = apply {
        dirty = false
    }

    fun markAsDirty() {
        dirty = true
    }
}
