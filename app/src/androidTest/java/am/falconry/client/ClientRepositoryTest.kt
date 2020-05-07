package am.falconry.client

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientDatabaseDao
import am.falconry.database.client.ClientEntity
import am.falconry.database.client.ClientRepository
import am.falconry.database.client.InterventionZoneEntity
import am.falconry.domain.Client
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

        val clientId = repository.saveClient(client, listOf())

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

        val updatedClientId = repository.saveClient(client, listOf())

        val savedClient = getValue(clientDao.getClient(updatedClientId))
        assertThat(savedClient).isNotNull()
        assertThat(savedClient!!.clientId).isEqualTo(clientId)
        assertThat(savedClient.name).isEqualTo("name.updated")
        assertThat(savedClient.email).isEqualTo("email.updated")
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateNewClientInterventionZone() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val client = Client(clientId, "name.updated", "email.updated")

        val updatedClientId = repository.saveClient(client, listOf())

        val savedClient = getValue(clientDao.getClient(updatedClientId))
        assertThat(savedClient).isNotNull()
        assertThat(savedClient!!.clientId).isEqualTo(clientId)
        assertThat(savedClient.name).isEqualTo("name.updated")
        assertThat(savedClient.email).isEqualTo("email.updated")
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
        assertThat(interventionZones[0].name).isEqualTo("interventionZone")
        assertThat(interventionZones[0].scaring).isTrue()
        assertThat(interventionZones[0].trapping).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateClientInterventionZone() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val client = getValue(repository.getClient(clientId))
        val interventionZone = InterventionZone(0L, "interventionZone", trapping = true, scaring = true)

        repository.saveClient(client, listOf(interventionZone))

        val interventionZones = getValue(clientDao.getAllClientInterventionZones(clientId))
        assertThat(interventionZones).isNotNull()
        assertThat(interventionZones).hasSize(1)
        assertThat(interventionZones[0].clientId).isEqualTo(clientId)
        assertThat(interventionZones[0].name).isEqualTo("interventionZone")
        assertThat(interventionZones[0].scaring).isTrue()
        assertThat(interventionZones[0].trapping).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateClientInterventionZone() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val client = getValue(repository.getClient(clientId))
        val interventionZoneId = clientDao.insertInterventionZone(defaultInterventionZone(clientId))
        val updatedInterventionZone = InterventionZone(interventionZoneId, "interventionZone.updated", trapping = false, scaring = false)

        repository.saveClient(client, listOf(updatedInterventionZone))

        val interventionZones = getValue(clientDao.getAllClientInterventionZones(clientId))
        assertThat(interventionZones).isNotNull()
        assertThat(interventionZones).hasSize(1)
        assertThat(interventionZones[0].interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(interventionZones[0].clientId).isEqualTo(clientId)
        assertThat(interventionZones[0].name).isEqualTo("interventionZone.updated")
        assertThat(interventionZones[0].scaring).isFalse()
        assertThat(interventionZones[0].trapping).isFalse()
    }

    private fun defaultInterventionZone(clientId: Long): InterventionZoneEntity {
        val interventionZone = InterventionZoneEntity()
        interventionZone.clientId = clientId
        interventionZone.name = "interventionZone"
        interventionZone.trapping = true
        interventionZone.scaring = true

        return interventionZone
    }

    private fun defaultClient(): ClientEntity {
        val client = ClientEntity()
        client.name = "Name"
        client.email = "email"

        return client
    }
}


