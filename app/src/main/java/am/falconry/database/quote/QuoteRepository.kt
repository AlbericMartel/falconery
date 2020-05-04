package am.falconry.database.quote

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientEntity
import am.falconry.domain.Quote
import am.falconry.domain.QuoteLocation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class QuoteRepository(database: FalconryDatabase) {

    private val quoteDao = database.quoteDatabaseDao

    fun getAllQuotes(): LiveData<List<Quote>> {
        val allQuotes = quoteDao.getAllQuotes()
        return Transformations.map(allQuotes) { quotes ->
            quotes.map {
                toDomainModel(it.quote, it.client, it.quoteLocations)
            }
        }
    }

    fun getQuote(quoteId: Long): LiveData<Quote> {
        val quoteWithLocations = quoteDao.getQuote(quoteId)

        if (quoteWithLocations.value == null) {
            return MutableLiveData(Quote.newQuote())
        }

        return Transformations.map(quoteWithLocations) {
            if (it != null) {
                toDomainModel(it.quote, it.client, it.quoteLocations)
            } else {
                Quote.newQuote()
            }
        }
    }

    fun saveQuoteLocations(quote: Quote, quoteLocations: MutableList<QuoteLocation>) {
        val quoteId = quoteDao.insertQuote(toEntity(quote))

        quoteLocations.forEach {
            quoteDao.insertQuoteLocation(toEntity(quoteId, it))
        }
    }

    private fun toDomainModel(quoteLocations: List<QuoteLocationAndLocation>): List<QuoteLocation> {
        return quoteLocations.map {
            QuoteLocation(it.location.locationId, it.location.locationId, it.location.name, it.location.trapping, it.location.scaring)
        }
    }

    private fun toDomainModel(quote: QuoteEntity, client: ClientEntity, interventions: List<QuoteLocationAndLocation>): Quote {
        return Quote(quote.quoteId, quote.onGoing, client.clientId, client.name, toDomainModel(interventions))
    }

    private fun toEntity(quote: Quote): QuoteEntity {
        return QuoteEntity(quote.quoteId, quote.clientId, quote.onGoing)
    }

    private fun toEntity(quoteId: Long, quoteLocation: QuoteLocation): QuoteLocationEntity {
        return QuoteLocationEntity(quoteLocation.quoteLocationId, quoteId, quoteLocation.locationId, quoteLocation.trapping, quoteLocation.scaring)
    }

}