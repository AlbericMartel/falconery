package am.falconry.database.client

import am.falconry.database.FalconryDatabase
import am.falconry.domain.Client
import am.falconry.domain.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

class ClientRepository(database: FalconryDatabase) {

    private val clientDao = database.clientDatabaseDao

    fun getAllClients(): LiveData<List<Client>> {
        val allClient = clientDao.getAllClients()
        return Transformations.map(allClient) { client ->
            client.map {
                toClientDomainModel(it)
            }
        }
    }

    fun getClient(clientId: Long): LiveData<Client> {
        val client = clientDao.getClient(clientId)

        return Transformations.map(client) {
            if (it != null) {
                toClientDomainModel(it)
            } else {
                Client.newClient()
            }
        }
    }

    fun getClientLocations(clientId: Long): LiveData<List<Location>> {
        val locations = clientDao.getAllClientLocations(clientId)
        return Transformations.map(locations) { locationEntities ->
            locationEntities.map { toLocationDomainModel(it) }
        }
    }

    fun saveClient(client: Client, locations: List<Location>): Long {
        var clientId = client.clientId
        val clientEntity = toClientEntity(client)
        if (clientId != 0L) {
            clientDao.updateClient(clientEntity)
        } else {
            clientId = clientDao.insertClient(clientEntity)
        }

        locations.forEach { saveLocation(clientId, it) }

        return clientId
    }

    private fun saveLocation(clientId: Long, location: Location) {
        val locationEntity = toLocationEntity(clientId, location)
        if (location.locationId != 0L) {
            clientDao.updateLocation(locationEntity)
        } else {
            clientDao.insertLocation(locationEntity)
        }
    }

    private fun toClientEntity(client: Client): ClientEntity {
        return ClientEntity(client.clientId, client.name, client.email)
    }

    private fun toLocationEntity(clientId: Long, location: Location): LocationEntity {
        return LocationEntity(location.locationId, clientId, location.name, location.trapping, location.scaring)
    }

    private fun toClientDomainModel(client: ClientEntity): Client {
        return Client(client.clientId, client.name, client.email)
    }

    private fun toLocationDomainModel(location: LocationEntity): Location {
        return Location(location.locationId, location.name, location.trapping, location.scaring)
    }

}