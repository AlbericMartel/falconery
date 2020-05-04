package am.falconry.database.quote

import am.falconry.database.client.LocationEntity
import androidx.room.Embedded
import androidx.room.Relation

data class QuoteLocationAndLocation(
    @Embedded val quote: QuoteLocationEntity,

    @Relation(parentColumn = "locationId", entityColumn = "locationId")
    val location: LocationEntity
)