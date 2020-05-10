package am.falconry.database.quote

import androidx.lifecycle.LiveData
import androidx.room.*
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
        "INSERT INTO quote_intervention (interventionPointId, quoteId, date, nbCaptures, comment) " +
                "SELECT ip.interventionPointId, :quoteId, :date, 0, '' " +
                "FROM quote q " +
                "JOIN intervention_zone iz ON q.interventionZoneId = iz.interventionZoneId " +
                "JOIN intervention_point ip ON iz.interventionZoneId = ip.interventionZoneId " +
                "WHERE quoteId = :quoteId"
    )
    fun insertInterventionDate(quoteId: Long, date: LocalDate)

    @Transaction
    @Query("SELECT * FROM quote_intervention WHERE quoteId = :quoteId AND  date = :date")
    fun getInterventionsForDate(quoteId: Long, date: LocalDate): LiveData<List<QuoteIntervention>>

    @Query("SELECT DISTINCT date FROM quote_intervention WHERE quoteId = :quoteId ORDER BY date")
    fun getAllInterventionDatesForQuote(quoteId: Long): LiveData<List<LocalDate>>

    @Update
    fun updateInterventions(interventions: List<QuoteInterventionEntity>)

    @Transaction
    @Query("SELECT * FROM quote_intervention WHERE quoteId = :quoteId ORDER BY date")
    fun getAllInterventionsForQuote(quoteId: Long): LiveData<List<QuoteIntervention>>
}
