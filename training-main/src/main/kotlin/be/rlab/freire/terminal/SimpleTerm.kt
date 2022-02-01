package be.rlab.freire.terminal

import com.google.common.base.Ascii
import com.jediterm.terminal.TtyConnector

class SimpleTerm {
    private val ESC = Ascii.ESC.toChar()
    private lateinit var tty: TtyConnector

    fun start() {
        tty = createTtyConnector()
        val screen = PtyScreenPanel(24, 80, tty)

        screen.start()
        Thread.sleep(500)
        val emulator = screen.emulator
        emulator.write("$ESC%G")
        emulator.write("$ESC[31m")
        emulator.write("Hello\r\n")
        emulator.write("$ESC[32;43m")
        emulator.write("World\r\n")
        emulator.write("foo")
        Thread.sleep(50)
        emulator.moveCursor(0, 0)
        emulator.write("bar")
        Thread.sleep(50)
        emulator.write("$ESC[31m")
        emulator.moveCursor(3, 2)
        emulator.write("ka")
    }

    private fun createTtyConnector(): TtyConnector {
        return PipedTtyConnector()
    }
}
