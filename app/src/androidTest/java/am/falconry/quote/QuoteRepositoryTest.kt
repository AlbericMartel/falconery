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
import am.falconry.database.quote.QuoteLocationEntity
import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.Location
import am.falconry.domain.Quote
import am.falconry.domain.QuoteLocation
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
    fun shouldGetNewQuoteWhenNotExistingQuoteId() {
        val quote = getValue(quoteRepository.getQuote(123))

        assertThat(quote).isNotNull()
        assertThat(quote.quoteId).isEqualTo(0L)
        assertThat(quote.onGoing).isFalse()
        assertThat(quote.clientName).isEqualTo("")
        val quoteLocations = quote.quoteLocations
        assertThat(quoteLocations).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetSingleQuoteLocation() {
        val clientId = clientDao.insertClient(givenClientEntity())
        val location = givenLocationEntity(getClient(clientId), "location1")
        val location1Id = clientDao.insertLocation(location)
        val quoteId = quoteDao.insertQuote(givenQuoteEntity(clientId))
        val quoteLocation1Id = quoteDao.insertQuoteLocation(givenQuoteLocationEntity(quoteId, location1Id, location.trapping, location.scaring))

        val quote = getValue(quoteRepository.getQuote(quoteId))

        assertThat(quote).isNotNull()
        assertThat(quote.quoteId).isEqualTo(quoteId)
        assertThat(quote.onGoing).isTrue()
        assertThat(quote.clientName).isEqualTo("Name")
        val quoteLocations = quote.quoteLocations
        assertThat(quoteLocations).hasSize(1)
        assertThat(quoteLocations[0].quoteLocationId).isEqualTo(quoteLocation1Id)
        assertThat(quoteLocations[0].locationId).isEqualTo(location1Id)
        assertThat(quoteLocations[0].locationName).isEqualTo("location1")
        assertThat(quoteLocations[0].trapping).isTrue()
        assertThat(quoteLocations[0].scaring).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun shouldGet2QuoteInterventions() {
        val clientId = clientDao.insertClient(givenClientEntity())
        val location1 = givenLocationEntity(getClient(clientId), "location1")
        val location1Id = clientDao.insertLocation(location1)
        val location2 = givenLocationEntity(getClient(clientId), "location2")
        val location2Id = clientDao.insertLocation(location2)
        val quoteId = quoteDao.insertQuote(givenQuoteEntity(clientId))
        val quoteLocation1Id = quoteDao.insertQuoteLocation(givenQuoteLocationEntity(quoteId, location1Id, location1.trapping, location1.scaring))
        val quoteLocation2Id = quoteDao.insertQuoteLocation(givenQuoteLocationEntity(quoteId, location2Id, location2.trapping, location2.scaring))

        val quotes = getValue(quoteRepository.getAllQuotes())

        assertThat(quotes).isNotNull()
        assertThat(quotes).hasSize(1)
        assertThat(quotes[0].quoteId).isEqualTo(quoteId)
        assertThat(quotes[0].onGoing).isTrue()
        assertThat(quotes[0].clientName).isEqualTo("Name")
        val quoteLocations = quotes[0].quoteLocations
        assertThat(quoteLocations).hasSize(2)
        assertThat(quoteLocations[0].quoteLocationId).isEqualTo(quoteLocation1Id)
        assertThat(quoteLocations[0].locationId).isEqualTo(location1Id)
        assertThat(quoteLocations[0].locationName).isEqualTo("location1")
        assertThat(quoteLocations[0].trapping).isTrue()
        assertThat(quoteLocations[0].scaring).isTrue()
        assertThat(quoteLocations[1].quoteLocationId).isEqualTo(quoteLocation2Id)
        assertThat(quoteLocations[1].locationId).isEqualTo(location2Id)
        assertThat(quoteLocations[1].locationName).isEqualTo("location2")
        assertThat(quoteLocations[1].trapping).isTrue()
        assertThat(quoteLocations[1].scaring).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun shouldSaveQuoteWithoutQuoteLocations() {
        val clientId = clientDao.insertClient(givenClientEntity())

        val quote = Quote.newQuote()
        quote.clientId = clientId
        quote.onGoing = true

        quoteRepository.saveQuoteLocations(quote, mutableListOf())

        val allQuotes = getValue(quoteRepository.getAllQuotes())
        assertThat(allQuotes).isNotEmpty()
        assertThat(allQuotes[0].clientName).isEqualTo("Name")
        assertThat(allQuotes[0].onGoing).isTrue()
        assertThat(allQuotes[0].quoteLocations).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun shouldSaveQuoteWithQuoteLocation() {
        val clientId = clientDao.insertClient(givenClientEntity())
        val locationId = clientDao.insertLocation(givenLocationEntity(getClient(clientId), "location1"))

        val quote = Quote.newQuote()
        quote.clientId = clientId
        quote.onGoing = true

        val location = Location.newLocation()
        location.locationId = locationId
        location.trapping = true
        location.scaring = true
        val quoteLocation = QuoteLocation.from(location)

        quoteRepository.saveQuoteLocations(quote, mutableListOf(quoteLocation))

        val allQuotes = getValue(quoteRepository.getAllQuotes())
        assertThat(allQuotes).isNotEmpty()
        val savedQuote = allQuotes[0]
        assertThat(savedQuote.clientName).isEqualTo("Name")
        assertThat(savedQuote.onGoing).isTrue()
        assertThat(savedQuote.quoteLocations).hasSize(1)
        val savedQuoteLocation = savedQuote.quoteLocations[0]
        assertThat(savedQuoteLocation.locationName).isEqualTo("location1")
        assertThat(savedQuoteLocation.trapping).isTrue()
        assertThat(savedQuoteLocation.scaring).isTrue()
    }

    private fun getClient(clientId: Long) = getValue(clientDao.getClient(clientId))!!

    private fun givenClientEntity(): ClientEntity {
        val client = ClientEntity()
        client.name = "Name"
        client.email = "email"

        return client
    }

    private fun givenLocationEntity(client: ClientEntity, name: String): LocationEntity {
        val location = LocationEntity()
        location.clientId = client.clientId
        location.name = name
        location.trapping = true
        location.scaring = true

        return location
    }

    private fun givenQuoteEntity(clientId: Long): QuoteEntity {
        val quote = QuoteEntity()
        quote.clientId = clientId
        quote.onGoing = true

        return quote
    }

    private fun givenQuoteLocationEntity(quoteId: Long, locationId: Long, trapping: Boolean, scaring: Boolean): QuoteLocationEntity {
        val quoteLocation = QuoteLocationEntity()
        quoteLocation.quoteId = quoteId
        quoteLocation.locationId = locationId
        quoteLocation.trapping = trapping
        quoteLocation.scaring = scaring

        return quoteLocation
    }
}


