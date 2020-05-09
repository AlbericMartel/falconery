package am.falconry.database.quote

import am.falconry.database.FalconryDatabase
import am.falconry.domain.Quote
import am.falconry.domain.QuoteIntervention
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import java.time.LocalDate

class QuoteRepository(database: FalconryDatabase) {

    private val quoteDao = database.quoteDatabaseDao

    fun createNewQuote(interventionZoneId: Long): Long {
        val newQuote = Quote.newQuote(interventionZoneId)

        return quoteDao.insertQuote(toEntity(newQuote))
    }

    fun getAllClientQuotes(clientId: Long): LiveData<List<Quote>> {
        return Transformations.map(quoteDao.getAllClientQuotes(clientId)) { quotes ->
            quotes.map { toDomainModel(it) }
        }
    }

    fun getQuote(quoteId: Long): LiveData<Quote> {
        return Transformations.map(quoteDao.getQuote(quoteId)) {
            toDomainModel(it)
        }
    }

    fun insertInterventionDate(quoteId: Long, date: LocalDate) {
        quoteDao.insertInterventionDate(quoteId, date)
    }

    fun getInterventionsForDate(quoteId: Long, now: LocalDate): LiveData<List<QuoteIntervention>> {
        return Transformations.map(quoteDao.getInterventionsForDate(quoteId, now)) { interventions ->
            interventions.map { toDomainModel(it) }
        }
    }

    fun getAllInterventionDatesForQuote(quoteId: Long): LiveData<List<LocalDate>> {
        return quoteDao.getAllInterventionDatesForQuote(quoteId)
    }

    fun getAllInterventionsForQuote(quoteId: Long): LiveData<Map<LocalDate, List<QuoteIntervention>>> {
        return Transformations.map(quoteDao.getAllInterventionsForQuote(quoteId)) { interventions ->
            interventions.map { toDomainModel(it) }.groupBy { it.date }
        }
    }

    private fun toEntity(quote: Quote): QuoteEntity {
        return QuoteEntity(quote.quoteId, quote.interventionZoneId, quote.onGoing)
    }

    private fun toDomainModel(quote: QuoteAndInterventionZone): Quote {
        return Quote(quote.quote.quoteId, quote.quote.interventionZoneId, quote.interventionZone.name, quote.quote.onGoing)
    }

    private fun toDomainModel(quoteIntervention: QuoteInterventionEntity): QuoteIntervention {
        return QuoteIntervention(quoteIntervention.interventionId, quoteIntervention.quoteId, quoteIntervention.interventionPointId, quoteIntervention.date)
    }
}