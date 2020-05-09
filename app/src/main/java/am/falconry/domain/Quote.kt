package am.falconry.domain

data class Quote(
    var quoteId: Long,
    var interventionZoneId: Long,
    var interventionZoneName: String,
    var onGoing: Boolean
) {
    companion object {
        fun newQuote(interventionZoneId: Long): Quote {
            return Quote(0L, interventionZoneId, "", false)
        }
    }
}