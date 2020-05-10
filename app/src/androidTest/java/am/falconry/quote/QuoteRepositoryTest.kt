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
import am.falconry.database.client.InterventionPointEntity
import am.falconry.database.client.InterventionZoneEntity
import am.falconry.database.quote.QuoteDatabaseDao
import am.falconry.database.quote.QuoteEntity
import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.Quote
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
    fun shouldCreateNewQuote() {
        val clientId = clientDao.insertClient(givenClient())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZone(getClient(clientId), "interventionZone1"))

        val quoteId = quoteRepository.createNewQuote(interventionZoneId)

        val savedQuote = getValue(quoteDao.getQuote(quoteId))
        assertThat(savedQuote).isNotNull()
        assertThat(savedQuote.quote.quoteId).isEqualTo(quoteId)
        assertThat(savedQuote.interventionZone.interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(savedQuote.interventionZone.name).isEqualTo("interventionZone1")
        assertThat(savedQuote.quote.onGoing).isFalse()
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetAllClientQuotes() {
        val clientId = clientDao.insertClient(givenClient())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZone(getClient(clientId), "interventionZone1"))
        val quoteId = quoteDao.insertQuote(givenQuote(interventionZoneId))

        val clientQuotes = getValue(quoteRepository.getAllClientQuotes(clientId))

        assertThat(clientQuotes).isNotNull()
        assertThat(clientQuotes).hasSize(1)
        val singleQuote: Quote = clientQuotes[0]
        assertThat(singleQuote.quoteId).isEqualTo(quoteId)
        assertThat(singleQuote.interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(singleQuote.interventionZoneName).isEqualTo("interventionZone1")
        assertThat(singleQuote.onGoing).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetQuote() {
        val clientId = clientDao.insertClient(givenClient())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZone(getClient(clientId), "interventionZone1"))
        val quoteId = quoteDao.insertQuote(givenQuote(interventionZoneId))

        val quote = getValue(quoteRepository.getQuote(quoteId))

        assertThat(quote).isNotNull()
        assertThat(quote.quoteId).isEqualTo(quoteId)
        assertThat(quote.interventionZoneId).isEqualTo(interventionZoneId)
        assertThat(quote.interventionZoneName).isEqualTo("interventionZone1")
        assertThat(quote.onGoing).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun shouldAddInterventionDate() {
        val clientId = clientDao.insertClient(givenClient())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZone(getClient(clientId), "interventionZone"))
        val interventionPointId1 = clientDao.insertInterventionPoint(givenInterventionPoint(interventionZoneId, "interventionPoint1"))
        val interventionPointId2 = clientDao.insertInterventionPoint(givenInterventionPoint(interventionZoneId, "interventionPoint2"))
        val quoteId = quoteDao.insertQuote(givenQuote(interventionZoneId))
        val now = LocalDate.now()

        quoteRepository.insertInterventionDate(quoteId, now)

        val interventions = getValue(quoteRepository.getInterventionsForDate(quoteId, now))
        assertThat(interventions).isNotNull()
        assertThat(interventions).hasSize(2)
        assertThat(interventions.map { it.interventionPointId }).containsExactlyElementsIn(listOf(interventionPointId1, interventionPointId2))
        assertThat(interventions.map { it.interventionPointName }).containsExactlyElementsIn(listOf("interventionPoint1", "interventionPoint2"))
        assertThat(interventions.map { it.date }).contains(now)
        assertThat(interventions.map { it.nbCaptures }).contains("0")
        assertThat(interventions.map { it.comment }).contains("")
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateIntervention() {
        val clientId = clientDao.insertClient(givenClient())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZone(getClient(clientId), "interventionZone"))
        val interventionPointId = clientDao.insertInterventionPoint(givenInterventionPoint(interventionZoneId, "interventionPoint"))
        val quoteId = quoteDao.insertQuote(givenQuote(interventionZoneId))
        val now = LocalDate.now()
        quoteRepository.insertInterventionDate(quoteId, now)
        val intervention = getValue(quoteRepository.getInterventionsForDate(quoteId, now))[0]
        intervention.nbCaptures = "10"
        intervention.comment = "Finished with eagles"

        quoteRepository.updateInterventions(listOf(intervention))

        val interventionUpdated = getValue(quoteRepository.getInterventionsForDate(quoteId, now))[0]
        assertThat(interventionUpdated).isNotNull()
        assertThat(interventionUpdated.interventionId).isEqualTo(intervention.interventionId)
        assertThat(interventionUpdated.interventionPointId).isEqualTo(interventionPointId)
        assertThat(interventionUpdated.quoteId).isEqualTo(quoteId)
        assertThat(interventionUpdated.date).isEqualTo(now)
        assertThat(interventionUpdated.nbCaptures).isEqualTo("10")
        assertThat(interventionUpdated.comment).isEqualTo("Finished with eagles")
    }

    @Test
    @Throws(Exception::class)
    fun shouldUpdateInterventionWithEmptyNbCaptures() {
        val clientId = clientDao.insertClient(givenClient())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZone(getClient(clientId), "interventionZone"))
        clientDao.insertInterventionPoint(givenInterventionPoint(interventionZoneId, "interventionPoint"))
        val quoteId = quoteDao.insertQuote(givenQuote(interventionZoneId))
        val now = LocalDate.now()
        quoteRepository.insertInterventionDate(quoteId, now)
        val intervention = getValue(quoteRepository.getInterventionsForDate(quoteId, now))[0]
        intervention.nbCaptures = ""

        quoteRepository.updateInterventions(listOf(intervention))

        val interventionUpdated = getValue(quoteRepository.getInterventionsForDate(quoteId, now))[0]
        assertThat(interventionUpdated.nbCaptures).isEqualTo("0")
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetAllInterventionDatesForQuote() {
        val clientId = clientDao.insertClient(givenClient())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZone(getClient(clientId), "interventionZone"))
        clientDao.insertInterventionPoint(givenInterventionPoint(interventionZoneId, "interventionPoint"))
        val quoteId = quoteDao.insertQuote(givenQuote(interventionZoneId))
        val now = LocalDate.now()
        val nowPlus1Day = now.plusDays(1)

        quoteRepository.insertInterventionDate(quoteId, now)
        quoteRepository.insertInterventionDate(quoteId, nowPlus1Day)

        val interventions = getValue(quoteRepository.getAllInterventionDatesForQuote(quoteId))
        assertThat(interventions).isNotNull()
        assertThat(interventions).hasSize(2)
        assertThat(interventions).containsExactly(now, nowPlus1Day)
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetAllQuoteInterventions() {
        val clientId = clientDao.insertClient(givenClient())
        val interventionZoneId = clientDao.insertInterventionZone(givenInterventionZone(getClient(clientId), "interventionZone"))
        val interventionPointId = clientDao.insertInterventionPoint(givenInterventionPoint(interventionZoneId, "interventionPoint"))
        val quoteId = quoteDao.insertQuote(givenQuote(interventionZoneId))
        val now = LocalDate.now()
        val nowPlus1Day = now.plusDays(1)

        quoteRepository.insertInterventionDate(quoteId, now)
        quoteRepository.insertInterventionDate(quoteId, nowPlus1Day)

        val interventions = getValue(quoteRepository.getAllInterventionsForQuote(quoteId))
        assertThat(interventions).isNotNull()
        assertThat(interventions).hasSize(2)
        assertThat(interventions.keys).containsExactly(now, nowPlus1Day)
        assertThat(interventions[now]!!.map { it.interventionPointId }).containsExactly(interventionPointId)
        assertThat(interventions[now]!!.map { it.interventionPointName }).containsExactly("interventionPoint")
        assertThat(interventions[nowPlus1Day]!!.map { it.interventionPointId }).containsExactly(interventionPointId)
        assertThat(interventions[nowPlus1Day]!!.map { it.interventionPointName }).containsExactly("interventionPoint")
    }

    private fun getClient(clientId: Long) = getValue(clientDao.getClient(clientId))!!

    private fun givenClient(): ClientEntity {
        val client = ClientEntity()
        client.name = "Name"
        client.email = "email"

        return client
    }

    private fun givenInterventionZone(client: ClientEntity, name: String): InterventionZoneEntity {
        val interventionZone = InterventionZoneEntity()
        interventionZone.clientId = client.clientId
        interventionZone.name = name

        return interventionZone
    }

    private fun givenInterventionPoint(interventionZoneId: Long, name: String): InterventionPointEntity {
        val interventionPoint = InterventionPointEntity()
        interventionPoint.interventionZoneId = interventionZoneId
        interventionPoint.name = name

        return interventionPoint
    }

    private fun givenQuote(interventionZoneId: Long): QuoteEntity {
        val quote = QuoteEntity()
        quote.interventionZoneId = interventionZoneId
        quote.onGoing = true

        return quote
    }
}


