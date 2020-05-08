package am.falconry.database.client

import am.falconry.database.FalconryDatabase
import am.falconry.domain.Client
import am.falconry.domain.InterventionPoint
import am.falconry.domain.InterventionZone
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    fun getInterventionZoneByIdOrNewForClient(interventionZoneId: Long, clientId: Long): LiveData<InterventionZone> {
        if (interventionZoneId != 0L) {
            val interventionZone = clientDao.getInterventionZone(interventionZoneId)
            return Transformations.map(interventionZone) {
                if (it != null) {
                    toInterventionZoneDomainModel(it)
                } else {
                    InterventionZone.newInterventionZone()
                }
            }
        } else {
            return newInterventionZone(clientId)
        }
    }

    private fun newInterventionZone(clientId: Long): LiveData<InterventionZone> {
        val interventionZone = InterventionZone.newInterventionZone()
        interventionZone.clientId = clientId

        return Transformations.map(MutableLiveData(interventionZone)) {
            it
        }
    }

    fun getZoneInterventionPoints(interventionZoneId: Long): LiveData<List<InterventionPoint>> {
        val interventionPoints = clientDao.getAllInterventionPointsForInterventionZone(interventionZoneId)
        return Transformations.map(interventionPoints) { interventionPointsEntities ->
            interventionPointsEntities.map { toInterventionPointDomainModel(it) }
        }
    }

    fun saveClient(client: Client): Long {
        var clientId = client.clientId
        val clientEntity = toClientEntity(client)
        if (clientId != 0L) {
            clientDao.updateClient(clientEntity)
        } else {
            clientId = clientDao.insertClient(clientEntity)
        }

        return clientId
    }

    fun saveInterventionZone(clientId: Long, interventionZone: InterventionZone, interventionPoints: List<InterventionPoint>): Long {
        var interventionZoneId = interventionZone.interventionZoneId
        val interventionZoneEntity = toInterventionZoneEntity(clientId, interventionZone)
        if (interventionZone.interventionZoneId == 0L) {
            interventionZoneId = clientDao.insertInterventionZone(interventionZoneEntity)
        } else {
            clientDao.updateInterventionZone(interventionZoneEntity)
        }

        interventionPoints.forEach { saveInterventionPoint(interventionZoneId, it) }

        return interventionZoneId
    }

    private fun saveInterventionPoint(interventionZoneId: Long, interventionPoint: InterventionPoint) {
        val interventionPointEntity = toInterventionPointEntity(interventionZoneId, interventionPoint)
        if (interventionPoint.interventionPointId == 0L) {
            clientDao.insertInterventionPoint(interventionPointEntity)
        } else {
            clientDao.updateInterventionPoint(interventionPointEntity)
        }
    }

    private fun toClientEntity(client: Client): ClientEntity {
        return ClientEntity(client.clientId, client.name, client.email)
    }

    private fun toInterventionZoneEntity(clientId: Long, interventionZone: InterventionZone): InterventionZoneEntity {
        return InterventionZoneEntity(interventionZone.interventionZoneId, clientId, interventionZone.name)
    }

    private fun toInterventionPointEntity(interventionZoneId: Long, interventionPoint: InterventionPoint): InterventionPointEntity {
        return InterventionPointEntity(interventionPoint.interventionPointId, interventionZoneId, interventionPoint.name)
    }

    private fun toClientDomainModel(client: ClientEntity): Client {
        return Client(client.clientId, client.name, client.email)
    }

    private fun toInterventionZoneDomainModel(interventionZone: InterventionZoneEntity): InterventionZone {
        return InterventionZone(interventionZone.interventionZoneId, interventionZone.clientId, interventionZone.name)
    }

    private fun toInterventionPointDomainModel(interventionPoint: InterventionPointEntity): InterventionPoint {
        return InterventionPoint(interventionPoint.interventionPointId, interventionPoint.interventionPointId, interventionPoint.name)
    }

}