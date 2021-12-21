package be.rlab.freire.cpu.operations

import be.rlab.freire.cpu.MemoryRef
import be.rlab.freire.cpu.Op
import be.rlab.freire.cpu.Register

class Print(private val value: Any) : Op {
    override fun exec() {
        when (value) {
            is Register -> println("// register $value")
            is MemoryRef -> println("// memory address $value")
            else -> println("// $value")
        }
    }

    override fun toString(): String = "PRINT ${mkString(value)}"
}
