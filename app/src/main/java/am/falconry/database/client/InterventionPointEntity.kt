package am.falconry.database.client

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "intervention_point",
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
data class InterventionPointEntity(
        @PrimaryKey(autoGenerate = true)
        var interventionPointId: Long = 0L,

        var interventionZoneId: Long = 0L,

        @ColumnInfo(name = "name")
        var name: String = ""
)
