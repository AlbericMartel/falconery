package am.falconry.quote

import am.falconry.database.client.ClientRepository
import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.Location
import am.falconry.domain.Quote
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*

class QuoteViewModel(
    private val clientId: Long = 0L,
    private val clientRepository: ClientRepository,
    private val quoteRepository: QuoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var quote = MutableLiveData<Quote>()
    val clients = clientRepository.getAllClients()
    private val _clientLocations = MutableLiveData<List<Location>>()

    val clientNames: LiveData<List<String>> = Transformations.map(clients) {
        it?.map { client -> client.name } ?: listOf()
    }

    val locationNames: LiveData<List<String>> = Transformations.map(_clientLocations) {
        it?.map { location -> location.name } ?: listOf()
    }

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
            quoteRepository.getQuote(quoteId)
        }
    }

//    fun onSelectClient(client: Client) {
//        uiScope.launch {
//            _clientLocations.value = loadClientLocations(client)
//        }
//    }

    fun onSelectClient(clientName: String) {
        uiScope.launch {
            _clientLocations.value = loadClientLocations(clientName)
        }
    }

    private suspend fun loadClientLocations(clientName: String): List<Location> {
        return withContext(Dispatchers.IO) {
            val clientId = clients.value?.first { it.name == clientName }?.clientId

            if (clientId == null) {
                listOf()
            } else {
                clientRepository.getClientLocations(clientId)
            }

        }
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