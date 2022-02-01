package be.rlab.freire.animation

import be.rlab.freire.animation.support.MetricsUtils.indexToPoint
import be.rlab.freire.terminal.DebugScreenPanel
import javax.swing.SwingUtilities

class DebugScreen(
    private val width: Int,
    height: Int
) : Screen {
    private val panel: DebugScreenPanel = DebugScreenPanel(width, height)

    override fun attach() {
        SwingUtilities.invokeLater {
            panel.start()
        }
    }

    override fun flush() {
        panel.repaint()
    }

    override fun draw(
        row: Int,
        column: Int,
        cells: List<Cell>
    ) {
        cells.forEachIndexed { index, cell ->
            if (cell.isDirty()) {
                val point: Point = indexToPoint(index, width)
                panel.writeAt(point.x.toInt(), point.y.toInt(), arrayOf(cell.content).toCharArray())
                cell.clean()
            }
        }
    }
}
