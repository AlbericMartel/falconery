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

package am.falconry.quote

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientDatabaseDao
import am.falconry.database.client.ClientEntity
import am.falconry.database.client.LocationEntity
import am.falconry.database.quote.QuoteDatabaseDao
import am.falconry.database.quote.QuoteEntity
import am.falconry.database.quote.QuoteInterventionEntity
import am.falconry.database.quote.QuoteRepository
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
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class QuoteRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var quoteRepository: QuoteRepository
    private lateinit var clientDao: ClientDatabaseDao
    private lateinit var quoteDao: QuoteDatabaseDao
    private lateinit var db: FalconryDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, FalconryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        quoteRepository = QuoteRepository(db)
        clientDao = db.clientDatabaseDao
        quoteDao = db.quoteDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun shouldGet2QuoteInterventions() {
        val clientId = clientDao.insertClient(givenClient())
        val location1Id = clientDao.insertLocation(givenLocation(clientDao.getClient(clientId)!!, "location1"))
        val location2Id = clientDao.insertLocation(givenLocation(clientDao.getClient(clientId)!!, "location2"))
        val quoteId = quoteDao.insertQuote(givenQuote(clientId))
        val now = LocalDate.now()
        val intervention1Id = quoteDao.insertQuoteIntervention(givenQuoteIntervention(quoteId, location1Id, now))
        val intervention2Id = quoteDao.insertQuoteIntervention(givenQuoteIntervention(quoteId, location2Id, now))

        val quotes = getValue(quoteRepository.getAllQuotes())

        assertThat(quotes).isNotNull()
        assertThat(quotes).hasSize(1)
        assertThat(quotes[0].quoteId).isEqualTo(quoteId)
        assertThat(quotes[0].onGoing).isTrue()
        assertThat(quotes[0].clientName).isEqualTo("Name")
        val interventions = quotes[0].interventions
        assertThat(interventions).hasSize(2)
        assertThat(interventions[0].interventionId).isEqualTo(intervention1Id)
        assertThat(interventions[0].locationId).isEqualTo(location1Id)
        assertThat(interventions[0].locationName).isEqualTo("location1")
        assertThat(interventions[1].interventionId).isEqualTo(intervention2Id)
        assertThat(interventions[1].locationId).isEqualTo(location2Id)
        assertThat(interventions[1].locationName).isEqualTo("location2")
    }

    private fun givenClient(): ClientEntity {
        val client = ClientEntity()
        client.name = "Name"
        client.email = "email"

        return client
    }

    private fun givenLocation(client: ClientEntity, name: String): LocationEntity {
        val location = LocationEntity()
        location.clientId = client.clientId
        location.name = name
        location.trapping = true
        location.scaring = true

        return location
    }

    private fun givenQuote(clientId: Long): QuoteEntity {
        val quote = QuoteEntity()
        quote.clientId = clientId
        quote.onGoing = true

        return quote
    }

    private fun givenQuoteIntervention(quoteId: Long, locationId: Long, date: LocalDate): QuoteInterventionEntity {
        val intervention = QuoteInterventionEntity()
        intervention.quoteId = quoteId
        intervention.locationId = locationId
        intervention.date = date

        return intervention
    }
}


