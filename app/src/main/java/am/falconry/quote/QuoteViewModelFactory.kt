package am.falconry.quote

import am.falconry.database.quote.QuoteRepository
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class QuoteViewModelFactory(
    private val clientId: Long,
    private val repository: QuoteRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteViewModel::class.java)) {
            return QuoteViewModel(clientId, repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}