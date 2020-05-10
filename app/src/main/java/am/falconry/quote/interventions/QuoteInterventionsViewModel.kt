package am.falconry.quote.interventions

import am.falconry.database.quote.QuoteRepository
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.time.LocalDate

class QuoteInterventionsViewModel(
    quoteId: Long,
    date: LocalDate,
    quoteRepository: QuoteRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val interventions = quoteRepository.getInterventionsForDate(quoteId, date)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}