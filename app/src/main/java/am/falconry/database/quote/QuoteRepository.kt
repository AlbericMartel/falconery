package am.falconry.database.quote

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientEntity
import am.falconry.domain.Quote
import am.falconry.domain.Quote2
import am.falconry.domain.QuoteInterventionZone
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class QuoteRepository(database: FalconryDatabase) {

    private val quoteDao = database.quoteDatabaseDao

    fun getAllQuotes(): LiveData<List<Quote>> {
        val allQuotes = quoteDao.getAllQuotes()
        return Transformations.map(allQuotes) { quotes ->
            quotes.map {
                toDomainModel(it.quote, it.client, it.quoteInterventionZones)
            }
        }
    }

    fun getQuote(quoteId: Long): LiveData<Quote> {
        val quoteWithInterventionZones = quoteDao.getQuote(quoteId)

        if (quoteWithInterventionZones.value == null) {
            return MutableLiveData(Quote.newQuote())
        }

        return Transformations.map(quoteWithInterventionZones) {
            if (it != null) {
                toDomainModel(it.quote, it.client, it.quoteInterventionZones)
            } else {
                Quote.newQuote()
            }
        }
    }

    fun saveQuoteInterventionZones(quote: Quote, quoteInterventionZones: MutableList<QuoteInterventionZone>) {
        val quoteId = quoteDao.insertQuote(toEntity(quote))

        quoteInterventionZones.forEach {
            quoteDao.insertQuoteInterventionZone(toEntity(quoteId, it))
        }
    }

    fun createNewQuote(interventionZoneId: Long): Long {
        val newQuote = Quote2.newQuote(interventionZoneId)

        return quoteDao.insertQuote(toEntity(newQuote))
    }

    fun getAllClientQuotes(clientId: Long): LiveData<List<Quote2>> {
        return Transformations.map(quoteDao.getAllClientQuotes(clientId)) {
            toDomainModel2(it)
        }
    }

    private fun toDomainModel(quoteInterventionZones: List<QuoteInterventionZoneAndInterventionZone>): List<QuoteInterventionZone> {
        return quoteInterventionZones.map {
            QuoteInterventionZone(it.interventionZone.interventionZoneId, it.interventionZone.interventionZoneId, it.interventionZone.name)
        }
    }

    private fun toDomainModel(quote: QuoteEntity, client: ClientEntity, interventions: List<QuoteInterventionZoneAndInterventionZone>): Quote {
        return Quote(quote.quoteId, quote.onGoing, client.clientId, client.name, toDomainModel(interventions))
    }

    private fun toDomainModel2(quotes: List<QuoteAndInterventionZone>): List<Quote2> {
        return quotes.map {
            Quote2(it.quote.quoteId, it.quote.interventionZoneId, it.interventionZone.name, it.quote.onGoing)
        }
    }

    private fun toEntity(quote: Quote): QuoteEntity {
        return QuoteEntity(quote.quoteId, quote.clientId, quote.onGoing)
    }

    private fun toEntity(quote: Quote2): QuoteEntity2 {
        return QuoteEntity2(quote.quoteId, quote.interventionZoneId, quote.onGoing)
    }

    private fun toEntity(quoteId: Long, quoteInterventionZone: QuoteInterventionZone): QuoteInterventionZoneEntity {
        return QuoteInterventionZoneEntity(quoteInterventionZone.quoteInterventionZoneId, quoteId, quoteInterventionZone.interventionZoneId)
    }

}