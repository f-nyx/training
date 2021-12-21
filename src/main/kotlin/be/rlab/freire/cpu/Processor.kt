package be.rlab.freire.cpu

import be.rlab.freire.cpu.operations.*

/** Processor for simple instructions to teach how a simple processing unit works.
 */
class Processor {
    val memory: Memory = Memory()
    val history: MutableList<Op> = mutableListOf()

    /** General-purpose CPU register. */
    var r01 = Register.new("r01")
    /** General-purpose CPU register. */
    var r02 = Register.new("r02")
    /** General-purpose CPU register. */
    var r03 = Register.new("r03")
    /** General-purpose CPU register. */
    var r04 = Register.new("r04")
    /** General-purpose CPU register. */
    var r05 = Register.new("r05")
    /** General-purpose CPU register. */
    var r06 = Register.new("r06")
    /** General-purpose CPU register. */
    var r07 = Register.new("r07")
    /** General-purpose CPU register. */
    var r08 = Register.new("r08")
    /** Result register.
     * Instructions that generates a result like ADD or SUB will store the result in this register.
     */
    var res = Register.new("res")
    /** Remainder register.
     * Instructions that generates a remainder like DIV will store the remainder in this register.
     */
    var rem = Register.new("rem")
    /** End register, indicates whether the program called the end() instruction. */
    var end = false
    /** Zero-flag. It might be -1, 0 or 1. */
    var zf: Int = 0

    fun ref(address: Long): MemoryRef = MemoryRef.byAddress(address)
    fun ref(label: String): MemoryRef = MemoryRef.byLabel(label)

    // Execution and memory instructions

    /** Moves data from a _source_ to a _target_ location.
     *
     * The locations can be either CPU registers, memory addresses or raw values.
     *
     * @param target Target location.
     * @param source Source location.
     */
    fun mov(target: Any, source: Any?) = exec(Mov(this, target, source))
    fun cat(operand1: Any, operand2: Any) = exec(Cat(this, operand1, operand2))
    fun ret(value: Any? = null) = exec(Ret(this, value))
    fun proc(name: String, callback: () -> Unit) = exec(Proc(this, name, callback))
    fun cmp(operand1: Any, operand2: Any) = exec(Cmp(this, operand1, operand2))
    fun print(value: Any) = exec(Print(value))
    fun alloc(address: Long, label: String? = null) = exec(Alloc(this, address, label))

    // Arithmetic instructions
    fun add(operand1: Any, operand2: Any) = exec(ArithmeticOp(this, operand1, operand2, ArithmeticOpType.ADD))
    fun sub(operand1: Any, operand2: Any) = exec(ArithmeticOp(this, operand1, operand2, ArithmeticOpType.SUB))
    fun div(operand1: Any, operand2: Any) = exec(ArithmeticOp(this, operand1, operand2, ArithmeticOpType.DIV))
    fun mul(operand1: Any, operand2: Any) = exec(ArithmeticOp(this, operand1, operand2, ArithmeticOpType.MUL))
    fun inc(target: Any, amount: Long) = exec(Inc(this, target, amount))

    // Branching and conditionals instructions
    fun jmp(label: String) = exec(Jmp(this, MemoryRef.byLabel(label), JmpOpType.JMP))
    fun jmp(address: Long) = exec(Jmp(this, MemoryRef.byAddress(address), JmpOpType.JMP))
    fun jz(label: String) = exec(Jmp(this, MemoryRef.byLabel(label), JmpOpType.JZ))
    fun jz(address: Long) = exec(Jmp(this, MemoryRef.byAddress(address), JmpOpType.JZ))
    fun je(label: String) = exec(Jmp(this, MemoryRef.byLabel(label), JmpOpType.JE))
    fun je(address: Long) = exec(Jmp(this, MemoryRef.byAddress(address), JmpOpType.JE))
    fun jnz(label: String) = exec(Jmp(this, MemoryRef.byLabel(label), JmpOpType.JNZ))
    fun jnz(address: Long) = exec(Jmp(this, MemoryRef.byAddress(address), JmpOpType.JNZ))
    fun jne(label: String) = exec(Jmp(this, MemoryRef.byLabel(label), JmpOpType.JNE))
    fun jne(address: Long) = exec(Jmp(this, MemoryRef.byAddress(address), JmpOpType.JNE))
    fun end() = exec(End(this))

    fun read(source: Any?): Any? {
        return when (source) {
            is Register -> source.value
            is MemoryRef -> memory.read(source)
            else -> source
        }
    }

    fun write(
        target: Any,
        source: Any?
    ): Any? {
        require(target is Register || target is MemoryRef) {
            "The first parameter must be either a Register or a memory address."
        }
        val value: Any? = read(source)

        when (target) {
            is Register -> target.value = value
            is MemoryRef -> {
                // Automatically allocates memory if the target is a memory address and it doesn't exist.
                if (!memory.has(target)) {
                    memory.alloc(target)
                }
                memory.write(target, value)
            }
            else -> throw RuntimeException("unknown target")
        }

        return value
    }

    fun setZeroFlag(value: Number) {
        zf = when (value) {
            0 -> 1
            else -> 0
        }
    }

    private fun exec(op: Op) {
        println(op)
        history += op
        op.exec()
    }
}
