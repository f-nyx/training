package be.rlab.catsitter

class PriceBreakdown(
    val catQuantity: Int,
    var totalVisits: Int,
    val sundaysAndHolidaysCount: Int,
    val keysDelivery: Boolean,
    val remoteZone: Boolean
) {
    fun total(): Int {
        var basePrice: Int = if (remoteZone) {
            150
        } else {
            0
        }

        if (catQuantity <= 2) {
            basePrice += when {
                totalVisits in 1..2 -> 700
                totalVisits in 3..4 -> 650
                else -> 600
            }

        } else {
            basePrice += when {
                totalVisits in 1..2 -> 850
                totalVisits in 3..4 -> 800
                else -> 700
            }
        }
        val baseTotal: Int = basePrice * totalVisits

        var additional: Int = sundaysAndHolidaysCount * 100
        if (keysDelivery == true) {
            additional += 200
        }
        return baseTotal + additional
    }
}
