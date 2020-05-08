package am.falconry.database.quote

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "quote_intervention",
    foreignKeys = [
        ForeignKey(
            entity = QuoteInterventionZoneEntity::class,
            parentColumns = ["quoteInterventionZoneId"],
            childColumns = ["quoteInterventionZoneId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index("quoteInterventionZoneId")
    ]
)
data class QuoteInterventionEntity(
    @PrimaryKey(autoGenerate = true)
    var interventionId: Long = 0L,

    var quoteInterventionZoneId: Long = 0L,

    var date: LocalDate = LocalDate.now()
)