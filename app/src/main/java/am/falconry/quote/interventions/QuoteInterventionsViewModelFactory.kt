package am.falconry.quote.interventions

import am.falconry.database.quote.QuoteRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDate

class QuoteInterventionsViewModelFactory(
    private val quoteId: Long,
    private val date: LocalDate,
    private val quoteRepository: QuoteRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteInterventionsViewModel::class.java)) {
            return QuoteInterventionsViewModel(quoteId, date, quoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}