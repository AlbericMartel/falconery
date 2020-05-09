package am.falconry.database.quote

import am.falconry.database.client.InterventionZoneEntity
import androidx.room.Embedded
import androidx.room.Relation

data class QuoteAndInterventionZone(
    @Embedded val quote: QuoteEntity2,

    @Relation(parentColumn = "interventionZoneId", entityColumn = "interventionZoneId")
    val interventionZone: InterventionZoneEntity
)