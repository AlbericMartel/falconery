package am.falconry.quote.list

import am.falconry.database.quote.QuoteRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class QuotesViewModel(
    repository: QuoteRepository,
    clientId: Long
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val quotes = repository.getAllClientQuotes(clientId)

    private val _navigateToQuoteInterventions = MutableLiveData<Long>()
    val navigateToQuoteInterventions: LiveData<Long>
        get() = _navigateToQuoteInterventions

    fun onQuoteClicked(quoteId: Long) {
        _navigateToQuoteInterventions.value = quoteId
    }

    fun onQuoteInterventionsNavigated() {
        _navigateToQuoteInterventions.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}