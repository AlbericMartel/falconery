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

package am.falconry.database.client

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "location",
        foreignKeys = [
                ForeignKey(
                        entity = Client::class,
                        parentColumns = ["clientId"],
                        childColumns = ["clientId"],
                        onDelete = CASCADE
                )
        ]
)
data class Location(
        @PrimaryKey(autoGenerate = true)
        var locationId: Long = 0L,

        var clientId: Long = 0L,

        @ColumnInfo(name = "name")
        var name: String = "",

        @ColumnInfo(name = "trapping")
        var trapping: Boolean = false,

        @ColumnInfo(name = "scaring")
        var scaring: Boolean = false
)
