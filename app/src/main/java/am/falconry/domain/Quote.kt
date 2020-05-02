package am.falconry.domain

data class Quote(
    var quoteId: Long,
    var onGoing: Boolean,
    var clientName: String,
    var interventions: List<QuoteIntervention>
)

data class QuoteIntervention(
    var interventionId: Long,
    var locationId: Long,
    var locationName: String
)

class QuoteFactory {

    companion object {
        fun newQuote(): Quote {
            return Quote(0L, false, "", mutableListOf())
        }

        fun newIntervention(location: Location): QuoteIntervention {
            return QuoteIntervention(0L, location.locationId, location.name)
        }
    }
}