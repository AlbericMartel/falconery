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