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
class QuoteDatabaseDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var clientDao: ClientDatabaseDao
    private lateinit var quoteDao: QuoteDatabaseDao
    private lateinit var db: FalconryDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, FalconryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        quoteDao = db.quoteDatabaseDao
        clientDao = db.clientDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndGetSingleQuote() {
        val clientId = clientDao.insertClient(defaultClient())
        val locationId = clientDao.insertLocation(defaultLocation(clientDao.getClient(clientId)!!))
        val quoteId = quoteDao.insertQuote(defaultQuote(clientId))
        val now = LocalDate.now()
        val interventionId = quoteDao.insertQuoteIntervention(quoteIntervention(quoteId, locationId, now))

        val quote = quoteDao.getQuote(quoteId)
        assertQuote(quote?.quote, quoteId, true)
        assertQuoteIntervention(quote!!.interventions, quoteId, interventionId, locationId, now)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndGetAllQuotes() {
        val clientId = clientDao.insertClient(defaultClient())
        val locationId = clientDao.insertLocation(defaultLocation(clientDao.getClient(clientId)!!))
        val quoteId = quoteDao.insertQuote(defaultQuote(clientId))
        val now = LocalDate.now()
        val interventionId = quoteDao.insertQuoteIntervention(quoteIntervention(quoteId, locationId, now))

        val quotes = getValue(quoteDao.getAllQuotes())
        assertThat(quotes).isNotNull()
        assertThat(quotes).hasSize(1)
        assertQuote(quotes[0].quote, quoteId, true)
        assertQuoteIntervention(quotes[0].interventions, quoteId, interventionId, locationId, now)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndGetAllQuotesForClient() {
        val clientId = clientDao.insertClient(defaultClient())
        val locationId = clientDao.insertLocation(defaultLocation(clientDao.getClient(clientId)!!))
        val quoteId = quoteDao.insertQuote(defaultQuote(clientId))
        val now = LocalDate.now()
        val interventionId = quoteDao.insertQuoteIntervention(quoteIntervention(quoteId, locationId, now))

        val quotes = getValue(quoteDao.getAllQuotesForClient(clientId))
        assertThat(quotes).isNotNull()
        assertThat(quotes).hasSize(1)
        assertQuote(quotes[0].quote, quoteId, true)
        assertQuoteIntervention(quotes[0].interventions, quoteId, interventionId, locationId, now)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndUpdateQuote() {
        val clientId = clientDao.insertClient(defaultClient())
        val quoteId = quoteDao.insertQuote(defaultQuote(clientId))

        val quote = quoteDao.getQuote(quoteId)

        quote!!.quote.onGoing = false
        quoteDao.updateQuote(quote.quote)

        val updatedQuote = quoteDao.getQuote(quoteId)
        assertThat(updatedQuote).isNotNull()
        assertQuote(updatedQuote?.quote, quoteId, false)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndUpdateQuoteIntervention() {
        val clientId = clientDao.insertClient(defaultClient())
        val locationId = clientDao.insertLocation(defaultLocation(clientDao.getClient(clientId)!!))
        val quoteId = quoteDao.insertQuote(defaultQuote(clientId))
        val now = LocalDate.now()
        val interventionId = quoteDao.insertQuoteIntervention(quoteIntervention(quoteId, locationId, now))

        val quote = quoteDao.getQuote(quoteId)

        val updatedDate = LocalDate.now()
        quote!!.interventions[0].date = updatedDate
        quoteDao.updateQuoteIntervention(quote.interventions[0])

        val updatedQuote = getValue(quoteDao.getQuoteInterventions(quoteId))
        assertThat(updatedQuote).isNotNull()
        assertQuoteIntervention(updatedQuote, quoteId, interventionId, locationId, updatedDate)
    }

    private fun assertQuote(quote: QuoteEntity?, quoteId: Long, isOnGoing: Boolean) {
        assertThat(quote).isNotNull();
        assertThat(quote?.quoteId).isEqualTo(quoteId)
        assertThat(quote?.onGoing).isEqualTo(isOnGoing)
    }

    private fun assertQuoteIntervention(
        interventions: List<QuoteInterventionEntity>,
        quoteId: Long,
        interventionId: Long,
        locationId: Long,
        date: LocalDate
    ) {
        assertThat(interventions).isNotNull();
        assertThat(interventions).hasSize(1);
        assertThat(interventions[0].interventionId).isEqualTo(interventionId)
        assertThat(interventions[0].quoteId).isEqualTo(quoteId)
        assertThat(interventions[0].locationId).isEqualTo(locationId)
        assertThat(interventions[0].date).isEqualTo(date)
    }

    private fun defaultClient(): ClientEntity {
        val client = ClientEntity()
        client.name = "Name"
        client.email = "email"

        return client
    }

    private fun defaultLocation(client: ClientEntity): LocationEntity {
        val location = LocationEntity()
        location.clientId = client.clientId
        location.name = "location"
        location.trapping = true
        location.scaring = true

        return location
    }

    private fun defaultQuote(clientId: Long): QuoteEntity {
        val quote = QuoteEntity()
        quote.clientId = clientId
        quote.onGoing = true

        return quote
    }

    private fun quoteIntervention(quoteId: Long, locationId: Long, date: LocalDate): QuoteInterventionEntity {
        val intervention = QuoteInterventionEntity()
        intervention.quoteId = quoteId
        intervention.locationId = locationId
        intervention.date = date

        return intervention
    }
}


