package am.falconry.quote.interventiondates

import am.falconry.database.quote.QuoteRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class QuoteInterventionDatesViewModelFactory(
    private val clientId: Long,
    private val quoteRepository: QuoteRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteInterventionDatesViewModel::class.java)) {
            return QuoteInterventionDatesViewModel(clientId, quoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}