package am.falconry.quote

import am.falconry.database.client.ClientRepository
import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.Client
import am.falconry.domain.Location
import am.falconry.domain.QuoteLocation
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*

class QuoteViewModel(
    quoteId: Long = 0L,
    private val clientRepository: ClientRepository,
    private val quoteRepository: QuoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val selectedClient = MutableLiveData<Client?>()
    private val selectedLocation = MutableLiveData<Location?>()

    private val _clients = clientRepository.getAllClients()
    private var _clientLocations = selectClientLocations()
    val clientNames = selectClientNames()
    val locationNames = selectLocationNames()

    var quote = quoteRepository.getQuote(quoteId)
    val quoteLocations = MutableLiveData<MutableList<QuoteLocation>>()

    private val _navigateToQuoteList = MutableLiveData<Boolean>()
    val navigateToQuoteList: LiveData<Boolean>
        get() = _navigateToQuoteList

    fun onSelectClient(clientName: String) {
        selectedClient.value = _clients.value?.first { it.name == clientName }
    }

    fun onSelectLocation(locationName: String) {
        selectedLocation.value = _clientLocations.value?.first { it.name == locationName }
    }

    fun addQuoteLocation() {
        selectedLocation.value?.let {
            val quoteLocations = this.quoteLocations.value ?: mutableListOf()

            if (locationNotAlreadyAdded(quoteLocations, it)) {
                quoteLocations.add(QuoteLocation.from(it))
                this.quoteLocations.value = quoteLocations
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

    fun doneNavigatingToQuoteList() {
        _navigateToQuoteList.value = null
    }

    private fun locationNotAlreadyAdded(
        quoteLocations: MutableList<QuoteLocation>,
        location: Location
    ) = quoteLocations.none { intervention -> intervention.locationId == location.locationId }

    private fun saveQuote() {
        val client = selectedClient.value
        val editedQuote = quote.value

        if (client != null && editedQuote != null) {
            editedQuote.clientId = client.clientId

            val areAllLocationsValid = quoteLocations.value?.distinctBy { it.locationName }?.size == quoteLocations.value?.size ?: true
            if (areAllLocationsValid) {
                val updatedLocations = quoteLocations.value ?: mutableListOf()
                quoteRepository.saveQuoteLocations(editedQuote, updatedLocations)
            }
        }
    }

    private fun selectClientLocations(): LiveData<List<Location>> {
        return Transformations.switchMap(selectedClient) {
            if (it == null) {
                MutableLiveData()
            } else {
                clientRepository.getClientLocations(it.clientId)
            }
        }
    }

    private fun selectClientNames(): LiveData<List<String>> {
        return Transformations.map(_clients) {
            it?.map { client -> client.name } ?: listOf()
        }
    }

    private fun selectLocationNames(): LiveData<List<String>> {
        return Transformations.map(_clientLocations) {
            it?.map { location -> location.name } ?: listOf()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
