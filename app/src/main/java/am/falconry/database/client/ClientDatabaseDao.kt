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
    fun insertInterventionZone(interventionZone: InterventionZoneEntity): Long

    @Update
    fun updateInterventionZone(interventionZone: InterventionZoneEntity)

    @Query("SELECT * from intervention_zone WHERE interventionZoneId = :interventionZoneId")
    fun getInterventionZone(interventionZoneId: Long): InterventionZoneEntity?

    @Query("SELECT * FROM intervention_zone WHERE clientId = :clientId ORDER BY name DESC")
    fun getAllClientInterventionZones(clientId: Long): LiveData<List<InterventionZoneEntity>>
}
