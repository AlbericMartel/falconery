package am.falconry.database.client

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "intervention_zone",
        foreignKeys = [
                ForeignKey(
                        entity = ClientEntity::class,
                        parentColumns = ["clientId"],
                        childColumns = ["clientId"],
                        onDelete = CASCADE
                )
        ],
        indices = [
                Index("clientId")
        ]
)
data class InterventionZoneEntity(
        @PrimaryKey(autoGenerate = true)
        var interventionZoneId: Long = 0L,

        var clientId: Long = 0L,

        @ColumnInfo(name = "name")
        var name: String = ""
)
