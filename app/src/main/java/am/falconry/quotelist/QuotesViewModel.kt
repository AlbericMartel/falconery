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
    val repository: QuoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val quotes = repository.getAllQuotes()

    private val _navigateToQuoteInterventionZoneConf = MutableLiveData<Long>()
    val navigateToQuoteInterventionZoneConf: LiveData<Long>
        get() = _navigateToQuoteInterventionZoneConf

    private val _navigateToQuoteInterventions = MutableLiveData<Long>()
    val navigateToQuoteInterventions: LiveData<Long>
        get() = _navigateToQuoteInterventions

    fun onNewQuote() {
        _navigateToQuoteInterventionZoneConf.value = 0L
    }

    fun onQuoteInterventionZoneConfNavigated() {
        _navigateToQuoteInterventionZoneConf.value = null
    }

    fun onQuoteClicked(quoteInterventionZoneId: Long) {
        _navigateToQuoteInterventions.value = quoteInterventionZoneId
    }

    fun onQuoteInterventionsNavigated() {
        _navigateToQuoteInterventions.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}