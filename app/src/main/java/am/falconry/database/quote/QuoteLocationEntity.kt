package am.falconry.database.quote

import am.falconry.database.client.LocationEntity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "quote_location",
    foreignKeys = [
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["quoteId"],
            childColumns = ["quoteId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["locationId"],
            childColumns = ["locationId"],
            onDelete = CASCADE
        )
    ]
)
data class QuoteLocationEntity(
    @PrimaryKey(autoGenerate = true)
    var quoteLocationId: Long = 0L,

    var quoteId: Long = 0L,

    var locationId: Long = 0L,

    var trapping: Boolean = false,

    var scaring: Boolean = false
)