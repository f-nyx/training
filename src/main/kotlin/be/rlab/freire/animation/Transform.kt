package be.rlab.freire.animation

import be.rlab.freire.animation.support.MetricsUtils.pointToIndex

object Transform {
    // TODO: support all box-drawing characters (https://en.wikipedia.org/wiki/Box-drawing_character)
    private val horizontalReflectionMap: Map<Char, Char> = "[]][())(<>></\\\\/cɔɔc«»»«`´´`{}}{CↃↃCEƎƎE3ƐƐ3cɔɔc┐┌┌┐┘└└┘"
        .toList()
        .chunked(2)
        .associate { (source, destination) ->
            source to destination
        }
    private val verticalReflectionMap: Map<Char, Char> = "/\\\\/^v`,,'\"„„\".˙˙.‿⁀⁀‿∴∵∵∴_¯¯_4ᔭᔭ469967ⱢⱢ7A∀∀AFℲℲFG⅁⅁GJſſJL⅂⅂LMWWMNᴎᴎNPԀԀPQΌΌQRᴚᴚRT⊥⊥TU∩∩UVᴧᴧVY⅄⅄Yaɐɐabqqbdppdeǝǝefɟɟfgƃƃghɥɥhiııijɾɾjkʞʞkmɯɯmnuunrɹɹrtʇʇtvʌʌvwʍʍwyʎʎy"
        .toList()
        .chunked(2)
        .associate { (source, destination) ->
            source to destination
        }

    fun flip(
        figure: String,
        height: Int,
        width: Int,
        orientation: Orientation
    ): List<Cell> {
        return flip(Cell.frameFrom(figure, height, width), height, width, orientation)
    }

    /** Flips a figure and returns the new figure.
     *
     * ASCII characters have a direction, translating characters in a matrix is not like translating points,
     * it requires translating the character to the corresponding _reflection character_. This method uses a
     * finite and incomplete reflection map to translate characters. So, for instance, flipping a figure
     * horizontally will translate the character < (less than) into > (greater than).
     *
     * In order to translate positions in the plane, it multiplies the source matrix by a _reflection matrix_.
     * Reflection matrix for Y axis:
     * | -1  0 |
     * |  0  1 |
     * Reflection matrix for X axis:
     * |  1  0 |
     * |  0 -1 |
     *
     * The algorithm to apply this operation is quite simple. To flip a figure horizontally, it swaps the points
     * in a row from left to right. To flip a figure vertically, it swaps the points in a column from top to bottom.
     *
     * @param figure Figure to flip.
     * @param height Figure height.
     * @param width Figure width.
     * @param orientation Required reflection.
     * @return the transformed figure.
     */
    fun flip(
        figure: List<Cell>,
        height: Int,
        width: Int,
        orientation: Orientation
    ): List<Cell> {
        require(figure.size == height * width) { "Invalid number of elements, expected (height * width) elements" }
        // TODO copy layer from each cell.
        val transformed = Cell.frame(height, width, layer = 0).toMutableList()
        val reflectionMap = when (orientation) {
            Orientation.HORIZONTAL -> horizontalReflectionMap
            Orientation.VERTICAL -> verticalReflectionMap
        }

        for (row in 0 until height) {
            for (column in 0 until width) {
                val sourceIndex: Int = pointToIndex(Point.new(column, row), width)
                val targetIndex: Int = when (orientation) {
                    Orientation.HORIZONTAL -> pointToIndex(Point.new(width - column - 1, row), width)
                    Orientation.VERTICAL -> pointToIndex(Point.new(column, height - row - 1), width)
                }
                val sourceCell = figure[sourceIndex]
                transformed[targetIndex] = reflectionMap[sourceCell.content]?.let {
                    reflectionChar -> sourceCell.copy(content = reflectionChar)
                } ?: sourceCell
            }
        }
        return transformed
    }
}
