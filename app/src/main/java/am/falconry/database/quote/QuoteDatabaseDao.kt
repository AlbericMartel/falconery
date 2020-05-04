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
    @Query("SELECT * FROM quote WHERE quoteId = :quoteId")
    fun getQuote(quoteId: Long): LiveData<QuoteAndClientAndQuoteLocations?>

    @Transaction
    @Query("SELECT * FROM quote where clientId = :clientId")
    fun getAllQuotesForClient(clientId: Long): LiveData<List<QuoteAndClientAndQuoteLocations>>

    @Transaction
    @Query("SELECT * FROM quote")
    fun getAllQuotes(): LiveData<List<QuoteAndClientAndQuoteLocations>>

    @Insert
    fun insertQuoteLocation(intervention: QuoteLocationEntity): Long

    @Update
    fun updateQuoteLocation(intervention: QuoteLocationEntity)

    @Query("SELECT * FROM quote_location WHERE quoteId = :quoteId")
    fun getQuoteLocations(quoteId: Long): LiveData<List<QuoteLocationEntity>>

}
