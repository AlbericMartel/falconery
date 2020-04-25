/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.falconry.database

import am.falconry.database.client.Client
import am.falconry.database.client.ClientDatabaseDao
import am.falconry.database.client.Location
import am.falconry.database.quote.Quote
import am.falconry.database.quote.QuoteDatabaseDao
import am.falconry.database.quote.QuoteIntervention
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * A database that stores SleepNight information.
 * And a global method to get access to the database.
 *
 * This pattern is pretty much the same for any database,
 * so you can reuse it.
 */
@Database(entities = [Client::class, Location::class, Quote::class, QuoteIntervention::class], version = 1, exportSchema = false)
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
