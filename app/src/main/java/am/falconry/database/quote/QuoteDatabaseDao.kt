package am.falconry.database.quote

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface QuoteDatabaseDao {

    @Insert
    fun insertQuote(quote: QuoteEntity2): Long

    @Query("SELECT * FROM quote2 WHERE quoteId = :quoteId")
    fun getQuoteById(quoteId: Long): LiveData<QuoteEntity2>

    @Transaction
    @Query("SELECT * FROM quote2 q2 JOIN intervention_zone zone ON q2.interventionZoneId = zone.interventionZoneId WHERE zone.clientId = :clientId")
    fun getAllClientQuotes(clientId: Long): LiveData<List<QuoteAndInterventionZone>>
}
