package be.rlab.training

import be.rlab.freire.cpu.MemoryRef
import be.rlab.freire.cpu.Register
import be.rlab.freire.cpu.entryPoint
import be.rlab.freire.cpu.operations.Mov

/** Year 2135. Earth is done. For more than 80 years no government listened to the scientists that warned about
 * climate change. Things went bad, the soil stopped giving us food.
 *
 * You are part of a small group of scientists that discovered water in the exoplanet Proxima Centauri B
 * orbiting in the habitable zone of the red dwarf star Proxima Centauri. Your team has strong evidence that
 * there exist a kind of soil that could save the Earth. Your team managed to gather the last pieces of technology
 * that can enable an expedition to Proxima Centauri B, 4.2 light-years from Earth.
 *
 * You and your crew arrived to Proxima Centauri B, and the main ship is orbiting the exoplanet. You volunteer to
 * go down and run some tests on the soil, but something goes wrong. There was a miscalculation in the reentry window,
 * and you enter the atmosphere much faster than expected. You missed the landing zone, and your spaceship lands in
 * an unknown beach. You survived.
 *
 * First, you need to run a sanity check on instruments. You pick the spaceship CPU manual and read the summary:
 *
 * Your CPU is equipped with the following components:
 *
 * - 8 general-purpose registers.
 * - 1 result register.
 * - 1 remainder register.
 * - 7 Execution and Memory instructions.
 * - 4 Arithmetic instructions.
 * - 5 Branching and Conditionals instructions.
 * - 1 instruction to exit a program.
 *
 * In order to check that everything is in order with your spaceship, the CPU manual provides the following
 * instructions to run the sanity check:
 *
 * 1. All _even_ general-purpose registers (r02, r04, r06, r08) must read an _odd_ number from memory.
 * 2. All _odd_ general-purpose registers (r01, r03, r05, r07) must contain the result of subtracting 42
 * to the next register. For example, r01 must have the result of subtracting 42 to register r02.
 * 3. Print the result of each subtraction.
 * 4. If the result of the last subtraction is zero, print the text: "Empty", otherwise print the text: "Full".
 * 5. Exit the program successfully.
 */
val processor2 = entryPoint {
    mov(ref(0x1000), 11)
    mov(ref(0x1001), 13)
    mov(ref(0x1002), 15)
    mov(ref(0x1003), 43)
    mov(r02, ref(0x1000))
    mov(r04, ref(0x1001))
    mov(r06, ref(0x1002))
    mov(r08, ref(0x1003))

    sub(r02, 42)
    mov(r01, res)
    print(r01)

    sub(r04, 42)
    mov(r03, res)
    print(r03)

    sub(r06, 42)
    mov(r05, res)
    print(r05)

    sub(r08, 42)
    mov(r07, res)
    print(r07)

    // loop
    proc("loop") {
        print(r01)
        inc(r01, 1)
        cmp(r01, 10)
        jnz("loop")
    }
    mov(r01, 0)
    jmp("loop")

    end() // Ends the program.
}

// DO NOT CHANGE ANY CODE FROM HERE, IT VALIDATES THE EXERCISE.
fun main() {
    val movOps: List<Mov> = processor.history.filterIsInstance<Mov>()
    val evenRegisters: List<Register> = listOf(processor.r02, processor.r04, processor.r06, processor.r08)
    val oddRegisters: List<Register> = listOf(processor.r01, processor.r03, processor.r05, processor.r07)

    require(
        movOps.filter { op ->
            evenRegisters.contains(op.target)
                && op.source is MemoryRef
                && processor.read(op.source) is Number
                && (processor.read(op.source)!! as Number).toLong() % 2 != 0L
        }.size == evenRegisters.size
    ) { "Condition 1 is not fulfilled, even registers must read an odd number from memory" }
}
