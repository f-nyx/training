package be.rlab.catsitter

import org.junit.jupiter.api.Test

// This is a first-class function.
fun sum1(operand1: Int, operand2: Int): String {
    return (operand1 + operand2).toString()
}

// This is a variable of type function and its value is an anonymous function or lambda.
val sum2: (Int, Int) -> String = { operand1: Int, operand2: Int ->
    (operand1 + operand2).toString()
}

// This is a variable of type function and its value is an anonymous function or lambda.
val printHello: () -> Unit = { println("hello") }

/**
Functions are executable units.
Functions are also values. It means they can be assigned to variables, or take as parameter, or used as return values
from other functions.

There are two types of functions: first-class functions and anonymous functions or lambdas.

## Syntax

### First-class functions

1. keyword `fun`
2. name of the function: `sum`
3. function parameters between parenthesis: `(operand1: Int, operand2: Int)`
    a. Function parameters are optional
    b. Parenthesis are required
    c. Parameters are separated by commas
4. Function return type at the end, after colon: `: Int`
    a. Return type is optional. If not defined, default is `Unit`
5. `return` expression to return a value: `return operand1 + operand2`
    a. The return expression can be either an operation, a function call, or a variable.

### Anonymous functions or lambdas

 1. starts and ends with brackets: `{}`
 2. parameters go after the opening bracket and before the lambda arrow: { operand1: Int, operand2: Int ->
    a. parameter types are optional: { operand1, operand2 ->
    b. if the lambda takes no parameters, the lambda arrow must be omitted:
        val printHello: () -> Unit = { println("hello") }
 3. the function body goes after the lambda arrow and before the closing bracket: `-> println("hello") }`
 4. the return value is the last expression without using the return keyword: (operand1 + operand2).toString()

### Functions as executable units

1. println(sum1(5, 7))
2. println(sum2(10, 3))
3. printHello()

### Functions as values

1. assigned to a variable:
    val otherSum: (Int, Int) -> String = ::sum1
2. pass a function as parameter:
    fun callSum(sum: (Int, Int) -> String) {
      println(sum(5, 7))
    }
    callSum(::sum1)
    callSum(::sum2)
3. return a function from another function:
    fun createSum(): (Int, Int) -> String {
      return ::sum1
    }
    createSum()(5, 7)
    val otherSum: (Int, Int) -> String = createSum()
    otherSum(5, 7)
4. if the last function parameter is of type function, the lambda can go outside the calling parenthesis.
 */
fun printWithPrefix(prefix: String, createMessage: () -> String) {
    println("[$prefix] ${createMessage()}")
}

class FunctionTest {
    @Test
    fun testPrintWithPrefix() {
        printWithPrefix("FOO", { "Hello World" })
        printWithPrefix("FOO") { "Hello World" }
    }
}
