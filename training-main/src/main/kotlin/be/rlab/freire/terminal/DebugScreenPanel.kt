package be.rlab.freire.terminal

import java.awt.*
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel

class DebugScreenPanel(
    private val screenWidth: Int,
    private val screenHeight: Int
) : JPanel(BorderLayout()) {
    data class DrawOperation(
        val x: Int,
        val y: Int,
        val value: CharArray
    )

    val OS_NAME = System.getProperty("os.name").lowercase(Locale.getDefault())
    val FONT_SIZE: Int = 14

    private val isWindows = OS_NAME.startsWith("windows")
    private val isOS2 = OS_NAME.startsWith("os/2") || OS_NAME.startsWith("os2")
    private val isMac = OS_NAME.startsWith("mac")
    private val isLinux = OS_NAME.startsWith("linux")
    private val isUnix = !isWindows && !isOS2

    private val buffer: Queue<DrawOperation> = ArrayDeque()
    private var fontWidth: Int = 0
    private var fontHeight: Int = 0

    fun start() {
        showTerminal()
    }

    override fun paintComponent(gfx: Graphics) {
        synchronized(buffer) {
            setupAntialiasing(gfx)

            while (buffer.isNotEmpty()) {
                val drawOp = buffer.poll()
                val translatedX: Int = drawOp.x * fontWidth
                val translatedY: Int = drawOp.y * fontHeight
                gfx.color = Color.WHITE
                // The magic +2 here is to give lines a tiny bit of extra height to avoid clipping when rendering
                // some Apple emoji, which are slightly higher than the font metrics reported character height :(
                gfx.fillRect(translatedX, (drawOp.y - 1) * fontHeight, fontWidth, fontHeight + gfx.fontMetrics.descent + 2)
                gfx.color = Color.BLACK
                gfx.drawChars(drawOp.value, 0, drawOp.value.size, translatedX, translatedY)
            }
        }
    }

    fun writeAt(x: Int, y: Int, value: CharArray) {
        synchronized(buffer) {
            buffer.offer(DrawOperation(x, y, value))
        }
    }

    private fun showTerminal() {
        val container = JFrame("SceneScreen")

        container.contentPane.add(this)
        container.isLocationByPlatform = false
        container.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        container.isResizable = false

        setupFontMetrics()
        font = resolveTerminalFont()
        preferredSize = Dimension(screenWidth  * fontWidth, screenHeight * fontHeight)
        container.pack()

        container.isVisible = true
    }

    private fun resolveTerminalFont(): Font {
        val fontName: String = when {
            isWindows -> "Consolas"
            isMac -> "Menlo"
            else -> "Monospaced"
        }
        return Font(fontName, Font.PLAIN and Font.BOLD, FONT_SIZE)
    }

    private fun setupFontMetrics() {
        val image = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        val gfx = image.createGraphics()
        gfx.font = resolveTerminalFont()
        fontWidth = gfx.fontMetrics.charWidth('W')
        fontHeight = gfx.fontMetrics.height
        image.flush()
        gfx.dispose()
    }

    private fun setupAntialiasing(gfx: Graphics) {
        if (gfx is Graphics2D) {
            gfx.setRenderingHints(
                RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            )
        }
    }
}
