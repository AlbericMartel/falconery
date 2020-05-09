package am.falconry.database.quote

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import java.time.LocalDate

@Dao
interface QuoteDatabaseDao {

    @Insert
    fun insertQuote(quote: QuoteEntity): Long

    @Query("SELECT * FROM quote WHERE quoteId = :quoteId")
    fun getQuote(quoteId: Long): LiveData<QuoteAndInterventionZone>

    @Transaction
    @Query("SELECT * FROM quote q JOIN intervention_zone zone ON q.interventionZoneId = zone.interventionZoneId WHERE zone.clientId = :clientId")
    fun getAllClientQuotes(clientId: Long): LiveData<List<QuoteAndInterventionZone>>

    @Query(
        "INSERT INTO quote_intervention (interventionPointId, quoteId, date) " +
                "SELECT ip.interventionPointId, :quoteId, :date " +
                "FROM quote q " +
                "JOIN intervention_zone iz ON q.interventionZoneId = iz.interventionZoneId " +
                "JOIN intervention_point ip ON iz.interventionZoneId = ip.interventionZoneId " +
                "WHERE quoteId = :quoteId"
    )
    fun insertInterventionDate(quoteId: Long, date: LocalDate)

    @Query("SELECT * FROM quote_intervention WHERE quoteId = :quoteId AND  date = :date")
    fun getInterventionsForDate(quoteId: Long, date: LocalDate): LiveData<List<QuoteInterventionEntity>>

    @Query("SELECT * FROM quote_intervention WHERE quoteId = :quoteId ORDER BY date")
    fun getAllInterventionsForQuote(quoteId: Long): LiveData<List<QuoteInterventionEntity>>

    @Query("SELECT DISTINCT date FROM quote_intervention WHERE quoteId = :quoteId ORDER BY date")
    fun getAllInterventionDatesForQuote(quoteId: Long): LiveData<List<LocalDate>>
}
