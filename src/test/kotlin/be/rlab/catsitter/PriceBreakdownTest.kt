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
            totalVisits = 3,
            sundaysAndHolidaysCount = 0,
            keysDelivery = true,
            remoteZone = true
        )
        assert(price.total() == 2600)
    }
    @Test
    fun testTotal3() {
        val price = PriceBreakdown(
            catQuantity = 2,
            totalVisits = 4,
            sundaysAndHolidaysCount = 1,
            keysDelivery = false,
            remoteZone = true
        )
        assert(price.total() == 3300)
    }
    @Test
    fun testTotal4() {
        val price = PriceBreakdown(
            catQuantity = 2,
            totalVisits = 5,
            sundaysAndHolidaysCount = 1,
            keysDelivery = false,
            remoteZone = false
        )
        assert(price.total() == 3100)
    }
    @Test
    fun testTotal5() {
        val price = PriceBreakdown(
            catQuantity = 2,
            totalVisits = 6,
            sundaysAndHolidaysCount = 1,
            keysDelivery = true,
            remoteZone = false
        )
        assert(price.total() == 3900)
    }

    @Test
    fun testTotal6() {
        val price = PriceBreakdown(
            catQuantity = 3,
            totalVisits = 2,
            sundaysAndHolidaysCount = 1,
            keysDelivery = true,
            remoteZone = true
        )
        assert(price.total() == 2300)
    }

    @Test
    fun testTotal7() {
        val price = PriceBreakdown(
            catQuantity = 4,
            totalVisits = 3,
            sundaysAndHolidaysCount = 0,
            keysDelivery = true,
            remoteZone = true
        )
        assert(price.total() == 3050)
    }
    @Test
    fun testTotal8() {
        val price = PriceBreakdown(
            catQuantity = 5,
            totalVisits = 4,
            sundaysAndHolidaysCount = 1,
            keysDelivery = false,
            remoteZone = true
        )
        assert(price.total() == 3900)
    }
    @Test
    fun testTotal9() {
        val price = PriceBreakdown(
            catQuantity = 6,
            totalVisits = 5,
            sundaysAndHolidaysCount = 1,
            keysDelivery = false,
            remoteZone = false
        )
        assert(price.total() == 3600)
    }
    @Test
    fun testTotal10() {
        val price = PriceBreakdown(
            catQuantity = 7,
            totalVisits = 6,
            sundaysAndHolidaysCount = 1,
            keysDelivery = true,
            remoteZone = false
        )
        assert(price.total() == 4500)
    }
}
