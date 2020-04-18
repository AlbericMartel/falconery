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

package am.falconry.client

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.Client
import am.falconry.database.client.ClientDatabaseDao
import am.falconry.database.client.Location
import am.falconry.utils.getValue
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class ClientDatabaseDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var clientDao: ClientDatabaseDao
    private lateinit var db: FalconryDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, FalconryDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        clientDao = db.clientDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testClient() {
        val client = defaultClient()
        val createdClientId = clientDao.insertClient(client)

        val createdClient = clientDao.getClient(createdClientId)
        assertEquals(client.name, createdClient?.name)
        assertEquals(client.email, createdClient?.email)

        val savedClients = getValue(clientDao.getAllClients())
        assertThat(savedClients.size, Matchers.equalTo(1))
        assertEquals(client.name, savedClients[0].name)
        assertEquals(client.email, savedClients[0].email)

        val updatedEmail = "email.updated"
        createdClient?.email = updatedEmail
        createdClient?.let { clientDao.updateClient(it) }

        val updatedClient = clientDao.getClient(createdClientId)
        assertEquals(client.name, updatedClient?.name)
        assertEquals(updatedEmail, updatedClient?.email)
    }

    @Test
    @Throws(Exception::class)
    fun testLocation() {
        clientDao.insertClient(defaultClient())
        val savedClients = getValue(clientDao.getAllClients())
        val client = savedClients[0]

        val location = defaultLocation(client)
        val locationCreatedId = clientDao.insertLocation(location)

        val createdLocation = clientDao.getLocation(locationCreatedId)
        assertEquals(location.name, createdLocation?.name)
        assertEquals(location.trapping, createdLocation?.trapping)
        assertEquals(location.scaring, createdLocation?.scaring)

        val savedLocations = clientDao.getAllClientLocations(client.clientId)
        assertEquals(location.name, savedLocations[0].name)
        assertEquals(location.trapping, savedLocations[0].trapping)
        assertEquals(location.scaring, savedLocations[0].scaring)

        createdLocation?.name = "anotherName"
        createdLocation?.trapping = false
        createdLocation?.scaring = false
        clientDao.updateLocation(createdLocation!!)

        val updatedLocation = clientDao.getLocation(createdLocation.locationId)
        assertEquals(createdLocation.name, updatedLocation?.name)
        assertEquals(createdLocation.trapping, updatedLocation?.trapping)
        assertEquals(createdLocation.scaring, updatedLocation?.scaring)
    }

    private fun defaultLocation(client: Client): Location {
        val location = Location()
        location.clientId = client.clientId
        location.name = "location"
        location.trapping = true
        location.scaring = true

        return location
    }

    private fun defaultClient(): Client {
        val client = Client()
        client.name = "Name"
        client.email = "email"

        return client
    }
}


