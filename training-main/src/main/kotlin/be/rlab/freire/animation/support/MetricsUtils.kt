package be.rlab.freire.animation.support

import be.rlab.freire.animation.Point

object MetricsUtils {
    /** Returns the index of a point in the List that represents a matrix.
     * @param point Point to resolve the index for.
     * @param matrixWidth Width of the matrix.
     * @return the index in the list that represents the matrix.
     */
    fun pointToIndex(
        point: Point,
        matrixWidth: Int
    ): Int {
        return (point.y * matrixWidth + point.x).toInt()
    }

    /** Returns the point in a matrix from its position in the List that represents the matrix.
     * @param index Index in the List of the required Point.
     * @param width Matrix width.
     * @return the point at the specific position in the matrix.
     */
    fun indexToPoint(
        index: Int,
        width: Int
    ): Point = Point(
        x = (index % width).toDouble(),
        y = (index / width).toDouble()
    )
}

