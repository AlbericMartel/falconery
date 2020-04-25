package am.falconry.database.quote

import androidx.room.Embedded
import androidx.room.Relation

data class QuoteWithInterventions(
    @Embedded val quote: Quote,

    @Relation(parentColumn = "quoteId", entityColumn = "quoteId")
    val interventions: List<QuoteIntervention>
)