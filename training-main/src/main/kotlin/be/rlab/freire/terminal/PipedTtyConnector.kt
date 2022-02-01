package be.rlab.freire.terminal

import com.jediterm.terminal.TtyConnector
import java.io.PipedReader
import java.io.PipedWriter
import java.io.Reader
import java.nio.charset.Charset

class PipedTtyConnector : TtyConnector {
    private val writer: PipedWriter = PipedWriter()
    private val reader: Reader = PipedReader(writer)

    override fun close() {
        writer.close()
        reader.close()
    }

    override fun getName(): String? {
        return null
    }

    override fun read(buf: CharArray, offset: Int, length: Int): Int {
        return reader.read(buf, offset, length)
    }

    override fun write(bytes: ByteArray) {
        writer.write(bytes.toString(Charset.defaultCharset()))
        writer.flush()
    }

    override fun isConnected(): Boolean {
        return true
    }

    override fun write(string: String) {
        writer.write(string)
        writer.flush()
    }

    override fun waitFor(): Int {
        return 0
    }

    override fun ready(): Boolean {
        return reader.ready()
    }
}
