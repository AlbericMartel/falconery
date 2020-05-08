package am.falconry.client

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.*
import am.falconry.domain.Client
import am.falconry.domain.InterventionPoint
import am.falconry.domain.InterventionZone
import am.falconry.utils.getValue
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ClientRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var clientDao: ClientDatabaseDao
    private lateinit var repository: ClientRepository
    private lateinit var db: FalconryDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, FalconryDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        clientDao = db.clientDatabaseDao
        repository = ClientRepository(db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetClients() {
        val client = defaultClient()
        val createdClientId = clientDao.insertClient(client)

        val createdClient = getValue(repository.getAllClients())
        assertThat(createdClient).hasSize(1)
        assertThat(createdClient[0].clientId).isEqualTo(createdClientId)
        assertThat(createdClient[0].name).isEqualTo("Name")
        assertThat(createdClient[0].email).isEqualTo("email")
    }

    @Test
    @Throws(Exception::class)
    fun shouldLoadEmptyClient() {
        val client = getValue(repository.getClient(123L))
        assertThat(client.clientId).isEqualTo(0L)
        assertThat(client.name).isEmpty()
        assertThat(client.email).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun shouldLoadExistingClient() {
        val clientId = clientDao.insertClient(defaultClient())

        val client = getValue(repository.getClient(clientId))

        assertThat(client.clientId).isEqualTo(clientId)
        assertThat(client.name).isEqualTo("Name")
        assertThat(client.email).isEqualTo("email")
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateNewClient() {
        val client = Client(0L, "name", "email")

        val clientId = repository.saveClient(client)

        val savedClient = getValue(clientDao.getClient(clientId))

        assertThat(savedClient).isNotNull()
        assertThat(savedClient!!.clientId).isEqualTo(clientId)
        assertThat(savedClient.name).isEqualTo("name")
        assertThat(savedClient.email).isEqualTo("email")
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateExistingClient() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val client = Client(clientId, "name.updated", "email.updated")

        val updatedClientId = repository.saveClient(client)

        val savedClient = getValue(clientDao.getClient(updatedClientId))
        assertThat(savedClient).isNotNull()
        assertThat(savedClient!!.clientId).isEqualTo(clientId)
        assertThat(savedClient.name).isEqualTo("name.updated")
        assertThat(savedClient.email).isEqualTo("email.updated")
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateClientInterventionZone() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val interventionZone = InterventionZone(0L, clientId, "interventionZone")
        val interventionPoint = InterventionPoint(0L, 0L, "interventionPoint")

        val interventionZoneId = repository.saveInterventionZone(clientId, interventionZone, listOf(interventionPoint))

        val interventionZones = getValue(clientDao.getAllClientInterventionZones(clientId))
        assertThat(interventionZones).isNotNull()
        assertThat(interventionZones).hasSize(1)
        val interventionZoneSaved = interventionZones[0]
        assertThat(interventionZoneSaved.clientId).isEqualTo(clientId)
        assertThat(interventionZoneSaved.name).isEqualTo("interventionZone")

        val interventionPoints = getValue(clientDao.getAllInterventionPointsForInterventionZone(interventionZoneId))
        assertThat(interventionPoints).isNotNull()
        assertThat(interventionPoints).hasSize(1)
        assertThat(interventionPoints[0].name).isEqualTo("interventionPoint")
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateClientInterventionZone() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val interventionZoneId = clientDao.insertInterventionZone(defaultInterventionZone(clientId))
        val intervationPoint = defaultInterventionPoint(interventionZoneId)
        val interventionPointId = clientDao.insertInterventionPoint(intervationPoint)
        val updatedInterventionZone = InterventionZone(interventionZoneId, clientId, "interventionZone.updated")
        val updatedInterventionPoint = InterventionPoint(interventionPointId, interventionZoneId, "interventionPoint.updated")

        repository.saveInterventionZone(clientId, updatedInterventionZone, listOf(updatedInterventionPoint))

        val interventionZones = getValue(clientDao.getAllClientInterventionZones(clientId))
        assertThat(interventionZones).isNotNull()
        assertThat(interventionZones).hasSize(1)
        val interventionZoneUpdated = interventionZones[0]
        assertThat(interventionZoneUpdated.interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(interventionZoneUpdated.clientId).isEqualTo(clientId)
        assertThat(interventionZoneUpdated.name).isEqualTo("interventionZone.updated")

        val interventionPoints = getValue(clientDao.getAllInterventionPointsForInterventionZone(interventionZoneId))
        assertThat(interventionPoints).isNotNull()
        assertThat(interventionPoints).hasSize(1)
        assertThat(interventionPoints[0].name).isEqualTo("interventionPoint.updated")
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetClientInterventionZones() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val interventionZone = defaultInterventionZone(clientId)
        val interventionZoneId = clientDao.insertInterventionZone(interventionZone)

        val interventionZones = getValue(repository.getClientInterventionZones(clientId))

        assertThat(interventionZones).isNotNull()
        assertThat(interventionZones).hasSize(1)
        assertThat(interventionZones[0].interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(interventionZones[0].clientId).isEqualTo(clientId)
        assertThat(interventionZones[0].name).isEqualTo("interventionZone")
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetZoneInterventionPoints() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val interventionZone = defaultInterventionZone(clientId)
        val interventionZoneId = clientDao.insertInterventionZone(interventionZone)
        val interventionPoint = defaultInterventionPoint(interventionZoneId)
        val interventionPointId = clientDao.insertInterventionPoint(interventionPoint)

        val interventionPoints = getValue(repository.getZoneInterventionPoints(interventionZoneId))

        assertThat(interventionPoints).isNotNull()
        assertThat(interventionPoints).hasSize(1)
        assertThat(interventionPoints[0].interventionPointId).isEqualTo(interventionPointId)
        assertThat(interventionPoints[0].interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(interventionPoints[0].name).isEqualTo("interventionPoint")
    }

    private fun defaultClient(): ClientEntity {
        val client = ClientEntity()
        client.name = "Name"
        client.email = "email"

        return client
    }

    private fun defaultInterventionZone(clientId: Long): InterventionZoneEntity {
        val interventionZone = InterventionZoneEntity()
        interventionZone.clientId = clientId
        interventionZone.name = "interventionZone"

        return interventionZone
    }

    private fun defaultInterventionPoint(interventionZoneId: Long): InterventionPointEntity {
        val interventionPoint = InterventionPointEntity()
        interventionPoint.interventionZoneId = interventionZoneId
        interventionPoint.name = "interventionPoint"

        return interventionPoint
    }
}


