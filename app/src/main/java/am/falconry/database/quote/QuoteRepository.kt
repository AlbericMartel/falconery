package am.falconry.database.quote

import am.falconry.database.FalconryDatabase
import am.falconry.domain.Quote
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

class QuoteRepository(database: FalconryDatabase) {

    private val quoteDao = database.quoteDatabaseDao

    fun createNewQuote(interventionZoneId: Long): Long {
        val newQuote = Quote.newQuote(interventionZoneId)

        return quoteDao.insertQuote(toEntity(newQuote))
    }

    fun getAllClientQuotes(clientId: Long): LiveData<List<Quote>> {
        return Transformations.map(quoteDao.getAllClientQuotes(clientId)) {
            toDomainModel(it)
        }
    }

    private fun toDomainModel(quotes: List<QuoteAndInterventionZone>): List<Quote> {
        return quotes.map {
            Quote(it.quote.quoteId, it.quote.interventionZoneId, it.interventionZone.name, it.quote.onGoing)
        }
    }

    private fun toEntity(quote: Quote): QuoteEntity {
        return QuoteEntity(quote.quoteId, quote.interventionZoneId, quote.onGoing)
    }
}