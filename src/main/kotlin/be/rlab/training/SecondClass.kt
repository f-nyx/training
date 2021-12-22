package be.rlab.training

import be.rlab.freire.cpu.entryPoint

val processor4 = entryPoint {
    mov(ref(15012), 7)
    mov(r01, ref(15012))
    mov(ref(15013), 10)
    mov(r02, ref(15013))
    mov(ref(15014), 2)
    mov(r03, ref(15014))
    add(r01, r02)
    mov(r04, res)
    sub(r04, r03)
    print(res)
    end() // Ends the program.
    }


// DO NOT CHANGE ANY CODE FROM HERE, IT VALIDATES THE EXERCISE.
fun main() {
}
