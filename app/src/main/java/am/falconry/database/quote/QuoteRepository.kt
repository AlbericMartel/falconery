package am.falconry.database.quote

import am.falconry.database.FalconryDatabase
import am.falconry.database.client.ClientEntity
import am.falconry.domain.Quote
import am.falconry.domain.QuoteIntervention
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

class QuoteRepository(database: FalconryDatabase) {

    private val quoteDatabaseDao = database.quoteDatabaseDao
    private val clientDatabaseDao = database.clientDatabaseDao

    fun getAllQuotes(): LiveData<List<Quote>> {
        val allQuotes = quoteDatabaseDao.getAllQuotes()
        return Transformations.map(allQuotes) { quote ->
            quote.map {
                val client = clientDatabaseDao.getClient(it.quote.clientId)
                val interventions = toDomainModel(it.interventions)
                toDomainModel(it.quote, client!!, interventions)
            }
        }
    }

    private fun toDomainModel(interventions: List<QuoteInterventionEntity>): List<QuoteIntervention> {
        return interventions.map { quoteIntervention ->
            val location = clientDatabaseDao.getLocation(quoteIntervention.locationId)

            QuoteIntervention(quoteIntervention.interventionId, location!!.locationId, location.name)
        }
    }

    private fun toDomainModel(quote: QuoteEntity, client: ClientEntity, interventions: List<QuoteIntervention>): Quote {
        return Quote(quote.quoteId, quote.onGoing, client.name, interventions)
    }

}