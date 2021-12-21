package be.rlab.training

import be.rlab.freire.cpu.entryPoint

val processor = entryPoint {
    mov(ref(15012), 7)
    mov(r01, ref(15012))
    mov(ref(15013), 4)
    mov(r02, ref(15013))
    mov(r03, 5)
    add(r01, r02)
    mov(r04, res)
    add(r04, r03)
    print(res)
    end() // Ends the program.
}

// DO NOT CHANGE ANY CODE FROM HERE, IT VALIDATES THE EXERCISE.
fun main() {
}
