package be.rlab.freire.terminal

import com.jediterm.terminal.TtyConnector
import com.jediterm.terminal.emulator.TtyEmulator
import com.jediterm.terminal.model.PtyTerminal
import com.jediterm.terminal.model.StyleState
import com.jediterm.terminal.model.TerminalTextBuffer
import com.jediterm.terminal.ui.TerminalPanel
import com.jediterm.terminal.ui.SettingsProvider
import java.awt.*
import javax.swing.JFrame
import javax.swing.JLayeredPane
import javax.swing.JPanel

class PtyScreenPanel(
    lines: Int,
    columns: Int,
    tty: TtyConnector
) : JPanel(BorderLayout()) {

    val emulator: TtyEmulator

    init {
        val settingsProvider = SettingsProvider()
        val style = StyleState().apply {
            setDefaultStyle(settingsProvider.defaultStyle)
        }
        val buffer = TerminalTextBuffer(columns, lines, style)
        val terminal = PtyTerminal(settingsProvider, buffer, style)
        createPanel(settingsProvider, buffer, style)
        emulator = TtyEmulator(tty, terminal)
    }

    fun start() {
        showTerminal()

        Thread {
            while (!Thread.currentThread().isInterrupted && emulator.hasNext()) {
                emulator.next()
            }
            println("end")
        }.start()
    }

    private fun showTerminal() {
        val container = JFrame("SceneScreen")
        container.add(this)
        container.isLocationByPlatform = false
        container.pack()
        container.isVisible = true
        container.isResizable = false
        container.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

    private fun createPanel(
        settingsProvider: SettingsProvider,
        buffer: TerminalTextBuffer,
        style: StyleState
    ) {
        val panel = TerminalPanel(settingsProvider, buffer, style)
        val innerPanel = JLayeredPane().apply {
            isFocusable = false
            layout = SceneLayout()
            add(panel, SceneLayout.TERMINAL)
        }
        add(innerPanel, BorderLayout.CENTER)
        isFocusable = false
        panel.init()
        panel.isVisible = true
    }

    private class SceneLayout : LayoutManager {
        companion object {
            const val TERMINAL = "TERMINAL"
        }

        private var terminal: Component? = null

        override fun addLayoutComponent(name: String, comp: Component) {
            terminal = comp
        }

        override fun removeLayoutComponent(comp: Component) {
            terminal = null
        }

        override fun preferredLayoutSize(target: Container): Dimension {
            synchronized(target.treeLock) {
                val dim = Dimension(0, 0)
                val d = terminal!!.preferredSize
                dim.width = d.width.coerceAtLeast(dim.width)
                dim.height = d.height.coerceAtLeast(dim.height)
                val insets = target.insets
                dim.width += insets.left + insets.right
                dim.height += insets.top + insets.bottom
                return dim
            }
        }

        override fun minimumLayoutSize(target: Container): Dimension {
            synchronized(target.treeLock) {
                val dim = Dimension(0, 0)
                val d = terminal!!.minimumSize
                dim.width = d.width.coerceAtLeast(dim.width)
                dim.height = d.height.coerceAtLeast(dim.height)
                val insets = target.insets
                dim.width += insets.left + insets.right
                dim.height += insets.top + insets.bottom
                return dim
            }
        }

        override fun layoutContainer(target: Container) {
            synchronized(target.treeLock) {
                val insets = target.insets
                val top = insets.top
                val bottom = target.height - insets.bottom
                val left = insets.left
                val right = target.width - insets.right
                val scrollDim = Dimension(0, 0)
                terminal!!.setBounds(left, top, right - left - scrollDim.width, bottom - top)
            }
        }
    }
}