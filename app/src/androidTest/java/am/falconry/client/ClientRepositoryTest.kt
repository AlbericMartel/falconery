package am.falconry.client

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientDatabaseDao
import am.falconry.database.client.ClientEntity
import am.falconry.database.client.ClientRepository
import am.falconry.database.client.LocationEntity
import am.falconry.domain.Client
import am.falconry.domain.Location
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
        val client = repository.getClient(123L)
        assertThat(client.clientId).isEqualTo(0L)
        assertThat(client.name).isEmpty()
        assertThat(client.email).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun shouldLoadExistingClient() {
        val clientId = clientDao.insertClient(defaultClient())

        val client = repository.getClient(clientId)

        assertThat(client.clientId).isEqualTo(clientId)
        assertThat(client.name).isEqualTo("Name")
        assertThat(client.email).isEqualTo("email")
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateNewClient() {
        val client = Client(0L, "name", "email")

        val clientId = repository.saveClient(client, listOf())

        val savedClient = clientDao.getClient(clientId)
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

        val savedClient = clientDao.getClient(updatedClientId)
        assertThat(savedClient).isNotNull()
        assertThat(savedClient!!.clientId).isEqualTo(clientId)
        assertThat(savedClient.name).isEqualTo("name.updated")
        assertThat(savedClient.email).isEqualTo("email.updated")
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateNewClientLocation() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val client = Client(clientId, "name.updated", "email.updated")

        val updatedClientId = repository.saveClient(client, listOf())

        val savedClient = clientDao.getClient(updatedClientId)
        assertThat(savedClient).isNotNull()
        assertThat(savedClient!!.clientId).isEqualTo(clientId)
        assertThat(savedClient.name).isEqualTo("name.updated")
        assertThat(savedClient.email).isEqualTo("email.updated")
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetClientLocations() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val location = defaultLocation(clientId)
        val locationId = clientDao.insertLocation(location)

        val locations = repository.getClientLocations(clientId)

        assertThat(locations).isNotNull()
        assertThat(locations).hasSize(1)
        assertThat(locations[0].locationId).isEqualTo(locationId)
        assertThat(locations[0].name).isEqualTo("location")
        assertThat(locations[0].scaring).isTrue()
        assertThat(locations[0].trapping).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateClientLocation() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val client = repository.getClient(clientId)
        val location = Location(0L, "location", trapping = true, scaring = true)

        repository.saveClient(client, listOf(location))

        val locations = clientDao.getAllClientLocations(clientId)
        assertThat(locations).isNotNull()
        assertThat(locations).hasSize(1)
        assertThat(locations[0].clientId).isEqualTo(clientId)
        assertThat(locations[0].name).isEqualTo("location")
        assertThat(locations[0].scaring).isTrue()
        assertThat(locations[0].trapping).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateClientLocation() {
        val clientId = clientDao.insertClient(ClientEntity(0L, "name", "email"))
        val client = repository.getClient(clientId)
        val locationId = clientDao.insertLocation(defaultLocation(clientId))
        val updatedLocation = Location(locationId, "location.updated", trapping = false, scaring = false)

        repository.saveClient(client, listOf(updatedLocation))

        val locations = clientDao.getAllClientLocations(clientId)
        assertThat(locations).isNotNull()
        assertThat(locations).hasSize(1)
        assertThat(locations[0].locationId).isEqualTo(locationId)
        assertThat(locations[0].clientId).isEqualTo(clientId)
        assertThat(locations[0].name).isEqualTo("location.updated")
        assertThat(locations[0].scaring).isFalse()
        assertThat(locations[0].trapping).isFalse()
    }

    private fun defaultLocation(clientId: Long): LocationEntity {
        val location = LocationEntity()
        location.clientId = clientId
        location.name = "location"
        location.trapping = true
        location.scaring = true

        return location
    }

    private fun defaultClient(): ClientEntity {
        val client = ClientEntity()
        client.name = "Name"
        client.email = "email"

        return client
    }
}


