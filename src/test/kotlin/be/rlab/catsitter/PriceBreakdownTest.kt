package be.rlab.catsitter

import org.junit.jupiter.api.Test

/**
 * Run: runs all tests in the class or a single test
 * Debug: debugs all tests in the class or a single test
 * Run with coverage: shows the execution branches that are already tested or missing.
 */
class PriceBreakdownTest {
    @Test
    fun testTotal1() {
        val price = PriceBreakdown(
            catQuantity = 1,
            totalVisits = 2,
            sundaysAndHolidaysCount = 1,
            keysDelivery = true,
            remoteZone = true
        )
        assert(price.total() == 2000)
    }

    @Test
    fun testTotal2() {
        val price = PriceBreakdown(
            catQuantity = 1,
            totalVisits = 2,
            sundaysAndHolidaysCount = 1,
            keysDelivery = true,
            remoteZone = true
        )
        assert(price.total() == 3000)
    }
}
