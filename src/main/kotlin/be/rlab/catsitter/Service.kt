package be.rlab.catsitter

import java.util.*

class Service(
    val client: Client,
    val firstVisit: Date,
    val lastVisit: Date,
    val totalVisits: Int,
    val notes: String,
    val payment: Payment,
    val priceTotal: Int,
    val keys: Keys
)