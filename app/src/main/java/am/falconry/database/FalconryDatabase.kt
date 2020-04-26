package am.falconry.database

import am.falconry.database.client.ClientDatabaseDao
import am.falconry.database.client.ClientEntity
import am.falconry.database.client.LocationEntity
import am.falconry.database.quote.QuoteDatabaseDao
import am.falconry.database.quote.QuoteEntity
import am.falconry.database.quote.QuoteInterventionEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ClientEntity::class, LocationEntity::class, QuoteEntity::class, QuoteInterventionEntity::class], version = 1, exportSchema = false)
@TypeConverters(am.falconry.database.quote.TypeConverters::class)
abstract class FalconryDatabase : RoomDatabase() {

    abstract val clientDatabaseDao: ClientDatabaseDao
    abstract val quoteDatabaseDao: QuoteDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: FalconryDatabase? = null

        fun getInstance(context: Context): FalconryDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            FalconryDatabase::class.java,
                            "falconry_database"
                    )
                            // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
