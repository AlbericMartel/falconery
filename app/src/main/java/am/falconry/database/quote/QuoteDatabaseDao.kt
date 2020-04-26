package am.falconry.database.quote

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface QuoteDatabaseDao {

    @Insert
    fun insertQuote(quote: QuoteEntity): Long

    @Update
    fun updateQuote(quote: QuoteEntity)

    @Transaction
    @Query("SELECT * from quote WHERE quoteId = :quoteId")
    fun getQuote(quoteId: Long): QuoteWithInterventions?

    @Transaction
    @Query("SELECT * FROM quote where clientId = :clientId")
    fun getAllQuotesForClient(clientId: Long): LiveData<List<QuoteWithInterventions>>

    @Transaction
    @Query("SELECT * FROM quote")
    fun getAllQuotes(): LiveData<List<QuoteWithInterventions>>

    @Insert
    fun insertQuoteIntervention(intervention: QuoteInterventionEntity): Long

    @Update
    fun updateQuoteIntervention(intervention: QuoteInterventionEntity)

    @Query("SELECT * FROM quote_intervention WHERE quoteId = :quoteId ORDER BY date DESC")
    fun getQuoteInterventions(quoteId: Long): LiveData<List<QuoteInterventionEntity>>
}
