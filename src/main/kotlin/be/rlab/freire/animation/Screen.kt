package be.rlab.freire.animation

/** A Screen is an abstraction to draw in a screen device.
 */
interface Screen {

    fun attach()
    fun flush()

    fun draw(
        row: Int,
        column: Int,
        cells: List<Cell>
    )
}
