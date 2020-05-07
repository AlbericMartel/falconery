package am.falconry.database.quote

import am.falconry.database.client.InterventionZoneEntity
import androidx.room.Embedded
import androidx.room.Relation

data class QuoteInterventionZoneAndInterventionZone(
    @Embedded val quote: QuoteInterventionZoneEntity,

    @Relation(parentColumn = "interventionZoneId", entityColumn = "interventionZoneId")
    val interventionZone: InterventionZoneEntity
)