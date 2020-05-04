package am.falconry.domain

data class Quote(
    var quoteId: Long,
    var onGoing: Boolean,
    var clientId: Long,
    var clientName: String,
    var quoteLocations: List<QuoteLocation>
) {
    companion object {
        fun newQuote(): Quote {
            return Quote(0L, false, 0L, "", mutableListOf())
        }
    }
}

data class QuoteLocation(
    var quoteLocationId: Long,
    var locationId: Long,
    var locationName: String,
    var trapping: Boolean,
    var scaring: Boolean
) {
    companion object {
        fun from(location: Location): QuoteLocation {
            return QuoteLocation(0L, location.locationId, location.name, location.trapping, location.scaring)
        }
    }
}