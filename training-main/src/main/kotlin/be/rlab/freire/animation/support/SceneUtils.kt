package be.rlab.freire.animation.support

import be.rlab.freire.animation.Cell
import be.rlab.freire.animation.Rect

object SceneUtils {
    /** Extracts a region from a pixel matrix.
     *
     * The returned matrix references the pixels from the original matrix, so any change in the region will
     * impact the original matrix.
     *
     * If the required region height or width exceeds the matrix height or width,
     * it will shrink the region to the matrix dimensions.
     *
     * If either the region height or width are omitted, it will extend the region to the matrix
     * height or width.
     *
     * The start row cannot be greater than or equal to the matrix height.
     * The start column cannot be greater than or equal to the matrix width.
     *
     * @param matrix Matrix to extract the region from.
     * @param width Matrix width.
     * @param rect Required region information.
     *
     * @return The required region.
     */
    fun extractRegion(
        matrix: List<Cell>,
        width: Int,
        rect: Rect
    ): List<Cell> {
        val height: Int = matrix.size / width

        require(rect.top < height) { "The start row cannot be greater than or equal to the matrix height" }
        require(rect.left < width) { "The start column cannot be greater than or equal to the matrix width" }

        val resolvedHeight: Int = when {
            rect.height == -1 || rect.bottom > height -> height - rect.top
            else -> rect.height
        }
        val resolvedWidth: Int = when {
            rect.width == -1 || rect.right > width -> width - rect.left
            else -> rect.width
        }
        val size = resolvedHeight * resolvedWidth
        val region: MutableList<Cell> = Cell.frame(resolvedHeight, resolvedWidth, 0).toMutableList()

        for (index in 0 until size) {
            val row = rect.top + (index / resolvedWidth)
            val column = rect.left + (index % resolvedWidth)
            region[index] = matrix[row * width + column]
        }

        return region
    }
}
