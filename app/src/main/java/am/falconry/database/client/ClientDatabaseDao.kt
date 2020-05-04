package am.falconry.database.client

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClientDatabaseDao {

    @Insert
    fun insertClient(client: ClientEntity): Long

    @Update
    fun updateClient(client: ClientEntity)

    @Query("SELECT * FROM client WHERE clientId = :clientId")
    fun getClient(clientId: Long): LiveData<ClientEntity?>

    @Query("SELECT * FROM client ORDER BY name DESC")
    fun getAllClients(): LiveData<List<ClientEntity>>

    @Insert
    fun insertLocation(location: LocationEntity): Long

    @Update
    fun updateLocation(location: LocationEntity)

    @Query("SELECT * from location WHERE locationId = :locationId")
    fun getLocation(locationId: Long): LocationEntity?

    @Query("SELECT * FROM location WHERE clientId = :clientId ORDER BY name DESC")
    fun getAllClientLocations(clientId: Long): LiveData<List<LocationEntity>>
}
