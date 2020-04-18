package am.falconry.database.client

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClientDatabaseDao {

    @Insert
    fun insertClient(client: Client): Long

    @Update
    fun updateClient(client: Client)

    @Query("SELECT * from client WHERE clientId = :clientId")
    fun getClient(clientId: Long): Client?

    @Query("SELECT * FROM client ORDER BY name DESC")
    fun getAllClients(): LiveData<List<Client>>

    @Insert
    fun insertLocation(location: Location): Long

    @Update
    fun updateLocation(location: Location)

    @Query("SELECT * from location WHERE locationId = :locationId")
    fun getLocation(locationId: Long): Location?

    @Query("SELECT * FROM location WHERE clientId = :clientId ORDER BY name DESC")
    fun getAllClientLocations(clientId: Long): List<Location>
}
