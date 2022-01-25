package be.rlab.training

import be.rlab.freire.cpu.entryPoint

val processor5 = entryPoint {
    print("saltos incondicionales")
    proc("case1") {
        mov(r01, 1)
        add(r01, 3)
        print(res)
    }
    proc("case2") {
        mov(r03, 5)
        sub(r03, 2)
        print(res)
    }
    jmp("case1")
    jmp("case2")

    // saltos incondicionales version kotlin
    fun case1(operando1: Int, operando2: Int) {
        print(operando1 + operando2)
    }
    print("llamada a funcion case1")
    case1(1, 3)


    print("saltos condicionales")
    proc("caseNotZero") {
        print("no es cero")
    }
    proc("caseZero") {
        print("es cero")
    }
    mov(r03, 5)
    sub(r03, 5)
    jz("caseZero")
    jnz("caseNotZero")

    val minUserAge = 13
    // esto requiere ingresar un numero en la consola
    val age = readLine()!!.toInt()

    if (age < minUserAge) {
        print("e muy chico")
    } else {
        print("it is good")
    }

    //if (age > minUserAge) {
    //    print("it is good")
    //}

    // loop
    print("bucles")
    proc("loop1") {
        print(r01)
        inc(r01, 1)
        cmp(r01, 10)
        jnz("loop1")
    }
    mov(r01, 0)
    jmp("loop1")

    var counter = 0
    while(counter < age) {
        // le suma uno a counter y pisa el valor de counter con ese resultado
        // counter = counter + 1
        // le suma el valor de la derecha a counter
        println(counter)
        counter += 1
        // incrementa uno
        // counter++
    }
    do {
        // le suma uno a counter y pisa el valor de counter con ese resultado
        // counter = counter + 1
        // le suma el valor de la derecha a counter
        println(counter)
        counter += 1
        // incrementa uno
        // counter++
    } while(counter < age)

    end() // Ends the program.
}


// DO NOT CHANGE ANY CODE FROM HERE, IT VALIDATES THE EXERCISE.
fun main() {
}
