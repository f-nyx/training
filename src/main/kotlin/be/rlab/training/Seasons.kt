package be.rlab.training

object Seasons {
    private val seasons: List<String> = listOf("summer", "autumn", "winter", "spring")

    fun random(): String {
        return seasons.random()
    }
}
