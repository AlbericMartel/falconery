package am.falconry.database.quote

import am.falconry.database.client.InterventionPointEntity
import androidx.room.Embedded
import androidx.room.Relation

data class QuoteIntervention(
    @Embedded
    var intervention: QuoteInterventionEntity,

    @Relation(parentColumn = "interventionPointId", entityColumn = "interventionPointId")
    var interventionPoint: InterventionPointEntity
)