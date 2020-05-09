package am.falconry.domain

import java.time.LocalDate

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

data class QuoteIntervention(
    var interventionId: Long,
    var quoteId: Long,
    var interventionPointId: Long,
    var date: LocalDate
)