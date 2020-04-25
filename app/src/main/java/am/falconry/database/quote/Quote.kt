package am.falconry.database.quote

import am.falconry.database.client.Client
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "quote",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["clientId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Quote(
    @PrimaryKey(autoGenerate = true)
    var quoteId: Long = 0L,

    var clientId: Long = 0L,

    @ColumnInfo(name = "on_going")
    var onGoing: Boolean = false
)