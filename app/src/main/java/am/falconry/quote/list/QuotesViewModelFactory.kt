package am.falconry.quote.list

import am.falconry.database.quote.QuoteRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class QuotesViewModelFactory(
    private val dataSource: QuoteRepository,
    private val clientId: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuotesViewModel::class.java)) {
            return QuotesViewModel(dataSource, clientId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}