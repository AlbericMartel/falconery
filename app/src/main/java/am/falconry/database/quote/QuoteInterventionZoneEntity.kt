package am.falconry.database.quote

import am.falconry.database.client.InterventionZoneEntity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "quote_intervention_zone",
    foreignKeys = [
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["quoteId"],
            childColumns = ["quoteId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = InterventionZoneEntity::class,
            parentColumns = ["interventionZoneId"],
            childColumns = ["interventionZoneId"],
            onDelete = CASCADE
        )
    ]
)
data class QuoteInterventionZoneEntity(
    @PrimaryKey(autoGenerate = true)
    var quoteInterventionZoneId: Long = 0L,

    var quoteId: Long = 0L,

    var interventionZoneId: Long = 0L,

    var trapping: Boolean = false,

    var scaring: Boolean = false
)