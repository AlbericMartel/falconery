package am.falconry.database.client

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "intervention_zone",
        foreignKeys = [
                ForeignKey(
                        entity = ClientEntity::class,
                        parentColumns = ["clientId"],
                        childColumns = ["clientId"],
                        onDelete = CASCADE
                )
        ]
)
data class InterventionZoneEntity(
        @PrimaryKey(autoGenerate = true)
        var interventionZoneId: Long = 0L,

        var clientId: Long = 0L,

        @ColumnInfo(name = "name")
        var name: String = "",

        @ColumnInfo(name = "trapping")
        var trapping: Boolean = false,

        @ColumnInfo(name = "scaring")
        var scaring: Boolean = false
)
