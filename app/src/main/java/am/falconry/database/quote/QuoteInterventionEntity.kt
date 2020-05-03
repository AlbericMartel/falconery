package am.falconry.database.quote

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "quote_intervention",
    foreignKeys = [
        ForeignKey(
            entity = QuoteLocationEntity::class,
            parentColumns = ["quoteLocationId"],
            childColumns = ["quoteLocationId"],
            onDelete = CASCADE
        )
    ]
)
data class QuoteInterventionEntity(
    @PrimaryKey(autoGenerate = true)
    var interventionId: Long = 0L,

    var quoteLocationId: Long = 0L,

    var date: LocalDate = LocalDate.now()
)