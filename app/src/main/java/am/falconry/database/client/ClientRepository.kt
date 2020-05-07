package am.falconry.database.client

import am.falconry.database.FalconryDatabase
import am.falconry.domain.Client
import am.falconry.domain.InterventionZone
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

    fun getClientInterventionZones(clientId: Long): LiveData<List<InterventionZone>> {
        val interventionZones = clientDao.getAllClientInterventionZones(clientId)
        return Transformations.map(interventionZones) { interventionZoneEntities ->
            interventionZoneEntities.map { toInterventionZoneDomainModel(it) }
        }
    }

    fun saveClient(client: Client, interventionZones: List<InterventionZone>): Long {
        var clientId = client.clientId
        val clientEntity = toClientEntity(client)
        if (clientId != 0L) {
            clientDao.updateClient(clientEntity)
        } else {
            clientId = clientDao.insertClient(clientEntity)
        }

        interventionZones.forEach { saveInterventionZone(clientId, it) }

        return clientId
    }

    private fun saveInterventionZone(clientId: Long, interventionZone: InterventionZone) {
        val interventionZoneEntity = toInterventionZoneEntity(clientId, interventionZone)
        if (interventionZone.interventionZoneId != 0L) {
            clientDao.updateInterventionZone(interventionZoneEntity)
        } else {
            clientDao.insertInterventionZone(interventionZoneEntity)
        }
    }

    private fun toClientEntity(client: Client): ClientEntity {
        return ClientEntity(client.clientId, client.name, client.email)
    }

    private fun toInterventionZoneEntity(clientId: Long, interventionZone: InterventionZone): InterventionZoneEntity {
        return InterventionZoneEntity(interventionZone.interventionZoneId, clientId, interventionZone.name, interventionZone.trapping, interventionZone.scaring)
    }

    private fun toClientDomainModel(client: ClientEntity): Client {
        return Client(client.clientId, client.name, client.email)
    }

    private fun toInterventionZoneDomainModel(interventionZone: InterventionZoneEntity): InterventionZone {
        return InterventionZone(interventionZone.interventionZoneId, interventionZone.name, interventionZone.trapping, interventionZone.scaring)
    }

}