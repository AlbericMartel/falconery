package am.falconry.database.quote

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface QuoteDatabaseDao {

    @Insert
    fun insertQuote(quote: QuoteEntity): Long

    @Query("SELECT * FROM quote WHERE quoteId = :quoteId")
    fun getQuoteById(quoteId: Long): LiveData<QuoteEntity>

    @Transaction
    @Query("SELECT * FROM quote q JOIN intervention_zone zone ON q.interventionZoneId = zone.interventionZoneId WHERE zone.clientId = :clientId")
    fun getAllClientQuotes(clientId: Long): LiveData<List<QuoteAndInterventionZone>>
}
