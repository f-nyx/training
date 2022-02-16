package be.rlab.catsitter

import org.junit.jupiter.api.Test

class ListTest {
    /** Print the list removing spaces around each item.
     */
    @Test
    fun test1() {
        val numbers: List<String> = listOf(" one", "two ", " three ", "four ")
        val trimmedList1: List<String> = numbers.map(
            transform = { number: String ->
                number.trim()
            }
        )
        val trimmedList2: List<String> = numbers.map() { number: String ->
            number.trim()
        }
        val trimmedList3: List<String> = numbers.map { number ->
            number.trim()
        }
    }
}
