package am.falconry.quote

import am.falconry.database.client.ClientRepository
import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.*
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*

class QuoteViewModel(
    private val quoteId: Long = 0L,
    private val clientRepository: ClientRepository,
    private val quoteRepository: QuoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _clients = clientRepository.getAllClients()
    private val _clientLocations = MutableLiveData<List<Location>>()

    val clientNames: LiveData<List<String>> = Transformations.map(_clients) {
        it?.map { client -> client.name } ?: listOf()
    }

    val locationNames: LiveData<List<String>> = Transformations.map(_clientLocations) {
        it?.map { location -> location.name } ?: listOf()
    }

    var quote = MutableLiveData<Quote>()
    var selectedClient: Client? = null
    var selectedLocation: Location? = null
    var selectedDate: String? = null
    var trapping: Boolean = false
    var scaring: Boolean = false
    val quoteLocations = MutableLiveData<MutableList<QuoteLocation>>()

    private val _navigateToQuoteList = MutableLiveData<Boolean>()
    val navigateToQuoteList: LiveData<Boolean>
        get() = _navigateToQuoteList

    init {
        uiScope.launch {
            quote.value = getQuote()
        }
    }

    private suspend fun getQuote(): Quote {
        return withContext(Dispatchers.IO) {
            quoteRepository.getQuote(quoteId)
        }
    }

    fun onSelectClient(clientName: String) {
        uiScope.launch {
            if (selectedClient != null) {
                resetClientLocations()
            }

            val client = _clients.value?.first { it.name == clientName }
            selectedClient = client
            _clientLocations.value = loadClientLocations(client)
        }
    }

    private fun resetClientLocations() {
        _clientLocations.value = mutableListOf()
    }

    fun onSelectLocation(locationName: String) {
        val location = _clientLocations.value?.first { it.name == locationName }
        selectedLocation = location
    }

    fun addQuoteLocation() {
        selectedLocation?.let { location ->
            val quoteLocations = this.quoteLocations.value ?: mutableListOf()

            if (locationNotAlreadyAdded(quoteLocations, location)) {
                quoteLocations.add(QuoteFactory.newQuoteLocation(location))
                this.quoteLocations.value = quoteLocations
            }
        }
    }

    private fun locationNotAlreadyAdded(
        quoteLocations: MutableList<QuoteLocation>,
        location: Location
    ) = quoteLocations.none { intervention -> intervention.locationId == location.locationId }

    private suspend fun loadClientLocations(client: Client?): List<Location> {
        return withContext(Dispatchers.IO) {
            val clientId = client?.clientId

            if (clientId == null) {
                listOf()
            } else {
                clientRepository.getClientLocations(clientId)
            }

        }
    }

    fun updateTrappingOption(locationId: Long, checked: Boolean) {
        quoteLocations.value?.first { it.locationId == locationId }.also { it?.trapping = checked }
    }

    fun updateScaringOption(locationId: Long, checked: Boolean) {
        quoteLocations.value?.first { it.locationId == locationId }.also { it?.scaring = checked }
    }

    fun trySaveQuote() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                saveQuote()
            }
        }
        _navigateToQuoteList.value = true
    }

    private fun saveQuote() {
        if (!isQuoteValid()) return

        quote.value!!.clientId = selectedClient!!.clientId

        val areAllLocationsValid = quoteLocations.value?.distinctBy { it.locationName }?.size == quoteLocations.value?.size ?: true
        if (areAllLocationsValid) {
            val updatedLocations = quoteLocations.value ?: mutableListOf()
            quoteRepository.saveQuoteLocations(quote.value!!, updatedLocations)
        }
    }

    private fun isQuoteValid(): Boolean {
        return selectedClient != null && quote.value != null
    }

    fun doneNavigatingToQuoteList() {
        _navigateToQuoteList.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
