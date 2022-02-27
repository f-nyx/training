package be.rlab.catsitter

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket


class HttpTest {

    fun server() {
        println("[Server] loading server")
        val server = ServerSocket(9999)
        println("[Server] waiting for connection")
        val client = server.accept()
        println("[Server] client connected")
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.inputStream))
        println("[Server] waiting for client message")
        val inputText: String = input.readLine()
        println("[Server] Server receiving [$inputText]")
        output.println("ECHO: $inputText")
    }

    fun client() {
        println("[Client] loading client")
        println("[Client] connecting to server")
        // Socket implements the TCP/IP protocol
        val client = Socket("127.0.0.1", 9999)
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.inputStream))

        println("[Client] sending [Hello]")
        output.println("Hello")
        output.flush()
        val serverResponse: String = input.readLine()
        println("[Client] receiving [$serverResponse]")
        client.close()
    }

    @Test
    fun testClientAndServer() {
        // Initialize the server
        Thread { server() }.start()
        println("[Client] waiting for the server to be ready")
        Thread.sleep(2000)
        // Call the server from the client
        client()
    }

    @Test
    fun getGoogle() {
        // The host and port to be connected.
        val host: String = "google.com"
        val port: Int = 80
        // Create a TCP socket and connect to the host:port.
        val socket = Socket(host, port)
        // Create the input and output streams for the network socket.
        val input = BufferedReader(
            InputStreamReader(socket.getInputStream())
        )
        val writer = PrintWriter(socket.getOutputStream(), true)
        // Send request to the HTTP server.
        writer.println("GET /index.html HTTP/1.0")
        writer.println() // blank line separating header & body
        writer.flush()
        // Read the response and display on console.
        var line: String?
        // readLine() returns null if server close the network socket.
        while (input.readLine().also { line = it } != null) {
            println(line)
        }
        // Close the I/O streams.
        input.close()
        writer.close()
    }
}
