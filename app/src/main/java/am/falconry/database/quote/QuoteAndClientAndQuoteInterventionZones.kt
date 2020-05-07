package am.falconry.database.quote

import am.falconry.database.client.ClientEntity
import androidx.room.Embedded
import androidx.room.Relation

data class QuoteAndClientAndQuoteInterventionZones(
    @Embedded val quote: QuoteEntity,

    @Relation(parentColumn = "clientId", entityColumn = "clientId")
    val client: ClientEntity,

    @Relation(parentColumn = "quoteId", entityColumn = "quoteId", entity = QuoteInterventionZoneEntity::class)
    val quoteInterventionZones: List<QuoteInterventionZoneAndInterventionZone>
)