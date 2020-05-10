package am.falconry.quote.interventions

import am.falconry.database.quote.QuoteRepository
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.time.LocalDate

class QuoteInterventionsViewModel(
    quoteId: Long,
    date: LocalDate,
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val interventions = quoteRepository.getInterventionsForDate(quoteId, date)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun updateInterventions() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val interventionsToUpdate = interventions.value
                if (interventionsToUpdate != null) {
                    quoteRepository.updateInterventions(interventionsToUpdate)
                }
            }
        }
    }
}