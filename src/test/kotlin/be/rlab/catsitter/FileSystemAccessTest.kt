package be.rlab.catsitter

import be.rlab.tehanu.support.ObjectMapperFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import java.io.File

class FileSystemAccessTest {
    @Test
    fun testWriteString() {
        val price = PriceBreakdown(
            catQuantity = 1,
            totalVisits = 2,
            sundaysAndHolidaysCount = 1,
            keysDelivery = true,
            remoteZone = true
        )
//        assert(price.total() == 2000)
        val file = File("/home/matt/projects/training/freire/test.txt")
        file.writeText("""
            catQuantity=${price.catQuantity}
            totalVisits=${price.totalVisits}
            sundaysAndHolidaysCount=${price.sundaysAndHolidaysCount}
        """.trimIndent())

        val data: String = file.readText()
        val lines: List<String> = data.split("\n")
        val catQuantity: Int = lines[0].substringAfter("=").toInt()
        println(catQuantity)
    }

    /** JSON: JavaScript Object Notation.
     * Data format to transfer and save objects and it keeps the type information.
     *
     * Jackson: library to write and read JSON format.
     */
    @Test
    fun testWriteJSON() {
        val price = PriceBreakdown(
            catQuantity = 1,
            totalVisits = 2,
            sundaysAndHolidaysCount = 1,
            keysDelivery = true,
            remoteZone = true
        )
        val objectMapper: ObjectMapper = ObjectMapperFactory.snakeCaseMapper
        val file = File("/home/matt/projects/training/freire/test.txt")
        file.writeText(objectMapper.writeValueAsString(price))

        val data: String = file.readText()
        val newPrice: PriceBreakdown = objectMapper.readValue(data)
        println(newPrice.catQuantity)
    }
}
