package am.falconry.database.quote

import am.falconry.database.FalconryDatabase
import am.falconry.domain.Quote2
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

class QuoteRepository(database: FalconryDatabase) {

    private val quoteDao = database.quoteDatabaseDao

    fun createNewQuote(interventionZoneId: Long): Long {
        val newQuote = Quote2.newQuote(interventionZoneId)

        return quoteDao.insertQuote(toEntity(newQuote))
    }

    fun getAllClientQuotes(clientId: Long): LiveData<List<Quote2>> {
        return Transformations.map(quoteDao.getAllClientQuotes(clientId)) {
            toDomainModel2(it)
        }
    }

    private fun toDomainModel2(quotes: List<QuoteAndInterventionZone>): List<Quote2> {
        return quotes.map {
            Quote2(it.quote.quoteId, it.quote.interventionZoneId, it.interventionZone.name, it.quote.onGoing)
        }
    }

    private fun toEntity(quote: Quote2): QuoteEntity2 {
        return QuoteEntity2(quote.quoteId, quote.interventionZoneId, quote.onGoing)
    }
}