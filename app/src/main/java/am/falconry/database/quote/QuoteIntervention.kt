package am.falconry.database.quote

import am.falconry.database.client.Location
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "quote_intervention",
    foreignKeys = [
        ForeignKey(
            entity = Quote::class,
            parentColumns = ["quoteId"],
            childColumns = ["quoteId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Location::class,
            parentColumns = ["locationId"],
            childColumns = ["locationId"],
            onDelete = CASCADE
        )
    ]
)
data class QuoteIntervention(
    @PrimaryKey(autoGenerate = true)
    var interventionId: Long = 0L,

    var quoteId: Long = 0L,

    var locationId: Long = 0L,

    var date: LocalDate = LocalDate.now()
)