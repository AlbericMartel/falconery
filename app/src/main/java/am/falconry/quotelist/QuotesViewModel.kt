package am.falconry.quotelist

import am.falconry.database.quote.QuoteRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class QuotesViewModel(
    val database: QuoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val quotes = database.getAllQuotes()

    private val _navigateToQuote = MutableLiveData<Long>()
    val navigateToQuote: LiveData<Long>
        get() = _navigateToQuote

    fun onNewQuote() {
        _navigateToQuote.value = 0L
    }

    fun onQuoteClicked(id: Long) {
        _navigateToQuote.value = id
    }

    fun onQuoteDetailNavigated() {
        _navigateToQuote.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}