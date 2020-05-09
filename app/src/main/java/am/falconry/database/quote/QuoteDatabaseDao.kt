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
    fun getQuote(quoteId: Long): LiveData<QuoteAndClientAndQuoteInterventionZones?>

    @Transaction
    @Query("SELECT * FROM quote where clientId = :clientId")
    fun getAllQuotesForClient(clientId: Long): LiveData<List<QuoteAndClientAndQuoteInterventionZones>>

    @Transaction
    @Query("SELECT * FROM quote")
    fun getAllQuotes(): LiveData<List<QuoteAndClientAndQuoteInterventionZones>>

    @Insert
    fun insertQuoteInterventionZone(intervention: QuoteInterventionZoneEntity): Long

    @Update
    fun updateQuoteInterventionZone(intervention: QuoteInterventionZoneEntity)

    @Query("SELECT * FROM quote_intervention_zone WHERE quoteId = :quoteId")
    fun getQuoteInterventionZones(quoteId: Long): LiveData<List<QuoteInterventionZoneEntity>>

    @Insert
    fun insertQuote(quote: QuoteEntity2): Long

    @Query("SELECT * FROM quote2 WHERE quoteId = :quoteId")
    fun getQuoteById(quoteId: Long): LiveData<QuoteEntity2>

    @Transaction
    @Query("SELECT * FROM quote2 q2 JOIN intervention_zone zone ON q2.interventionZoneId = zone.interventionZoneId WHERE zone.clientId = :clientId")
    fun getAllClientQuotes(clientId: Long): LiveData<List<QuoteAndInterventionZone>>
}
