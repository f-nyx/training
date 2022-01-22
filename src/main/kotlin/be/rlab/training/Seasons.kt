package be.rlab.training

object Seasons {
    val seasons: List<String> = listOf("summer", "autumn", "winter", "spring")

    fun random(): String {
        return seasons.random()
    }
}
