package am.falconry.database.quote

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientEntity
import am.falconry.domain.Quote
import am.falconry.domain.QuoteFactory
import am.falconry.domain.QuoteLocation
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

class QuoteRepository(database: FalconryDatabase) {

    private val quoteDao = database.quoteDatabaseDao
    private val clientDao = database.clientDatabaseDao

    fun getAllQuotes(): LiveData<List<Quote>> {
        val allQuotes = quoteDao.getAllQuotes()
        return Transformations.map(allQuotes) { quote ->
            quote.map {
                val client = clientDao.getClient(it.quote.clientId)
                toDomainModel(it.quote, client!!, it.quoteLocations)
            }
        }
    }

    fun getQuote(quoteId: Long): Quote {
        val quoteWithLocations = quoteDao.getQuote(quoteId)

        if (quoteWithLocations != null) {
            val client = clientDao.getClient(quoteWithLocations.quote.clientId) ?: ClientEntity()
            return toDomainModel(quoteWithLocations.quote, client, quoteWithLocations.quoteLocations)
        }

        return QuoteFactory.newQuote()
    }

    fun saveQuoteLocations(quote: Quote, quoteLocations: MutableList<QuoteLocation>) {
        val quoteId = quoteDao.insertQuote(toEntity(quote))

        quoteLocations.forEach {
            quoteDao.insertQuoteLocation(toEntity(quoteId, it))
        }
    }

    private fun toDomainModel(quoteLocations: List<QuoteLocationEntity>): List<QuoteLocation> {
        return quoteLocations.map { quoteLocation ->
            val location = clientDao.getLocation(quoteLocation.locationId)

            QuoteLocation(quoteLocation.quoteLocationId, location!!.locationId, location.name, location.trapping, location.scaring)
        }
    }

    private fun toDomainModel(quote: QuoteEntity, client: ClientEntity, interventions: List<QuoteLocationEntity>): Quote {
        return Quote(quote.quoteId, quote.onGoing, client.clientId, client.name, toDomainModel(interventions))
    }

    private fun toEntity(quote: Quote): QuoteEntity {
        return QuoteEntity(quote.quoteId, quote.clientId, quote.onGoing)
    }

    private fun toEntity(quoteId: Long, quoteLocation: QuoteLocation): QuoteLocationEntity {
        return QuoteLocationEntity(quoteLocation.quoteLocationId, quoteId, quoteLocation.locationId, quoteLocation.trapping, quoteLocation.scaring)
    }

}