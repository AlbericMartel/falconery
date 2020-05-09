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
import am.falconry.database.client.InterventionZoneEntity
import am.falconry.database.quote.QuoteDatabaseDao
import am.falconry.database.quote.QuoteEntity2
import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.Quote2
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
    fun shouldCreateNewQuote() {
        val clientId = clientDao.insertClient(givenClientEntity())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZoneEntity(getClient(clientId), "interventionZone1"))

        val quoteId = quoteRepository.createNewQuote(interventionZoneId)

        val savedQuote = getValue(quoteDao.getQuoteById(quoteId))
        assertThat(savedQuote).isNotNull()
        assertThat(savedQuote.quoteId).isEqualTo(quoteId)
        assertThat(savedQuote.interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(savedQuote.onGoing).isFalse()
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetAllClientQuotes() {
        val clientId = clientDao.insertClient(givenClientEntity())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZoneEntity(getClient(clientId), "interventionZone1"))
        val quoteId = quoteDao.insertQuote(givenQuoteEntity2(interventionZoneId))

        val clientQuotes = getValue(quoteRepository.getAllClientQuotes(clientId))

        assertThat(clientQuotes).isNotNull()
        assertThat(clientQuotes).hasSize(1)
        val singleQuote: Quote2 = clientQuotes[0]
        assertThat(singleQuote.quoteId).isEqualTo(quoteId)
        assertThat(singleQuote.interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(singleQuote.interventionZoneName).isEqualTo("interventionZone1")
        assertThat(singleQuote.onGoing).isTrue()
    }

    private fun getClient(clientId: Long) = getValue(clientDao.getClient(clientId))!!

    private fun givenClientEntity(): ClientEntity {
        val client = ClientEntity()
        client.name = "Name"
        client.email = "email"

        return client
    }

    private fun givenInterventionZoneEntity(client: ClientEntity, name: String): InterventionZoneEntity {
        val interventionZone = InterventionZoneEntity()
        interventionZone.clientId = client.clientId
        interventionZone.name = name

        return interventionZone
    }

    private fun givenQuoteEntity2(interventionZoneId: Long): QuoteEntity2 {
        val quote = QuoteEntity2()
        quote.interventionZoneId = interventionZoneId
        quote.onGoing = true

        return quote
    }
}


