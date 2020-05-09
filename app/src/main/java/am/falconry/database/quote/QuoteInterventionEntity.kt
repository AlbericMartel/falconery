package am.falconry.database.quote

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "quote_intervention"
)
data class QuoteInterventionEntity(
    @PrimaryKey(autoGenerate = true)
    var interventionId: Long = 0L,

    //TODO add intervention point

    var date: LocalDate = LocalDate.now()
)