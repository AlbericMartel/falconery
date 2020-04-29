package am.falconry.quote

import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.Quote
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class QuoteViewModel(
    private val clientId: Long = 0L,
    private val repository: QuoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var quote = MutableLiveData<Quote>()

    private val _navigateToQuoteList = MutableLiveData<Boolean>()
    val navigateToQuoteList: LiveData<Boolean>
        get() = _navigateToQuoteList

    init {
        uiScope.launch {
            quote.value = getQuote(clientId)
        }
    }

    private suspend fun getQuote(quoteId: Long): Quote {
        return withContext(Dispatchers.IO) {
            repository.getQuote(quoteId)
        }
    }

    fun cancel() {
        _navigateToQuoteList.value = true
    }

    fun trySaveQuote() {
    }

    private fun saveQuote() {
//        if (quote.value == null) return
//
//        val areAllLocationsValid = locations.value?.all { location -> isLocationValid(location) } ?: true
//        if (areAllLocationsValid) {
//            val updatedLocations = locations.value ?: listOf()
//            repository.saveClient(client.value!!, updatedLocations)
//        }
    }

    private fun isQuoteValid(): Boolean {
        return false
    }

    fun doneNavigatingToQuoteList() {
        _navigateToQuoteList.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}