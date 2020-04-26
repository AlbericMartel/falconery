package am.falconry.database.client

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "client")
data class ClientEntity(
        @PrimaryKey(autoGenerate = true)
        var clientId: Long = 0L,

        @ColumnInfo(name = "name")
        var name: String = "",

        @ColumnInfo(name = "email")
        var email: String = ""
)
