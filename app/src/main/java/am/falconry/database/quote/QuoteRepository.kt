package am.falconry.database.quote

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientEntity
import am.falconry.domain.Quote
import am.falconry.domain.QuoteIntervention
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
                toDomainModel(it.quote, client!!, it.interventions)
            }
        }
    }

    fun getQuote(quoteId: Long): Quote {
        val quoteWithInterventions = quoteDao.getQuote(quoteId)

        if (quoteWithInterventions != null) {
            val client = clientDao.getClient(quoteWithInterventions.quote.clientId) ?: ClientEntity()
            return toDomainModel(quoteWithInterventions.quote, client, quoteWithInterventions.interventions)
        }

        return newQuote()
    }

    private fun newQuote(): Quote {
        return toDomainModel(QuoteEntity(), ClientEntity(), mutableListOf())
    }

    private fun toDomainModel(interventions: List<QuoteInterventionEntity>): List<QuoteIntervention> {
        return interventions.map { quoteIntervention ->
            val location = clientDao.getLocation(quoteIntervention.locationId)

            QuoteIntervention(quoteIntervention.interventionId, location!!.locationId, location.name)
        }
    }

    private fun toDomainModel(quote: QuoteEntity, client: ClientEntity, interventions: List<QuoteInterventionEntity>): Quote {
        return Quote(quote.quoteId, quote.onGoing, client.name, toDomainModel(interventions))
    }

}