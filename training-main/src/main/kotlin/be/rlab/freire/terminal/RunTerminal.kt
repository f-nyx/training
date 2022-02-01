package be.rlab.freire.terminal

import javax.swing.SwingUtilities

fun main(arg: Array<String>) {
    SwingUtilities.invokeLater {
//        SimpleTerm().start()
        val screen = DebugScreenPanel(120, 300)

        screen.start()
    }
}
