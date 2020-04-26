package am.falconry.database.quote

import androidx.room.Embedded
import androidx.room.Relation

data class QuoteWithInterventions(
    @Embedded val quote: QuoteEntity,

    @Relation(parentColumn = "quoteId", entityColumn = "quoteId")
    val interventions: List<QuoteInterventionEntity>
)