package am.falconry.database.quote

import am.falconry.database.client.InterventionPointEntity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "quote_intervention",
    foreignKeys = [
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["quoteId"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InterventionPointEntity::class,
            parentColumns = ["interventionPointId"],
            childColumns = ["interventionPointId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["quoteId", "interventionPointId"]),
        Index(value = ["quoteId", "date"])
    ]
)
data class QuoteInterventionEntity(
    @PrimaryKey(autoGenerate = true)
    var interventionId: Long = 0L,

    var quoteId: Long = 0L,

    var interventionPointId: Long = 0L,

    var date: LocalDate = LocalDate.now(),

    var nbCaptures: Int = 0,

    var comment: String = ""
)