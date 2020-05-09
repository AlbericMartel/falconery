package am.falconry.domain

data class Quote(
    var quoteId: Long,
    var onGoing: Boolean,
    var clientId: Long,
    var clientName: String,
    var quoteInterventionZones: List<QuoteInterventionZone>
) {
    companion object {
        fun newQuote(): Quote {
            return Quote(0L, false, 0L, "", mutableListOf())
        }
    }
}

data class Quote2(
    var quoteId: Long,
    var interventionZoneId: Long,
    var interventionZoneName: String,
    var onGoing: Boolean
) {
    companion object {
        fun newQuote(interventionZoneId: Long): Quote2 {
            return Quote2(0L, interventionZoneId, "", false)
        }
    }
}

data class QuoteInterventionZone(
    var quoteInterventionZoneId: Long,
    var interventionZoneId: Long,
    var interventionZoneName: String
) {
    companion object {
        fun from(interventionZone: InterventionZone): QuoteInterventionZone {
            return QuoteInterventionZone(0L, interventionZone.interventionZoneId, interventionZone.name)
        }
    }
}