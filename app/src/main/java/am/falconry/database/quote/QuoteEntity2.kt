package am.falconry.database.quote

import am.falconry.database.client.InterventionZoneEntity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quote2",
    foreignKeys = [
        ForeignKey(
            entity = InterventionZoneEntity::class,
            parentColumns = ["interventionZoneId"],
            childColumns = ["interventionZoneId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index("interventionZoneId")
    ]
)
data class QuoteEntity2(
    @PrimaryKey(autoGenerate = true)
    var quoteId: Long = 0L,

    var interventionZoneId: Long = 0L,

    var onGoing: Boolean = false
)