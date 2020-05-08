package am.falconry.quote

import am.falconry.database.client.ClientRepository
import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.Client
import am.falconry.domain.InterventionZone
import am.falconry.domain.QuoteInterventionZone
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
    private val selectedInterventionZone = MutableLiveData<InterventionZone?>()

    private val _clients = clientRepository.getAllClients()
    private var _clientInterventionZones = selectClientInterventionZones()
    val clientNames = selectClientNames()
    val interventionZoneNames = selectInterventionZoneNames()

    var quote = quoteRepository.getQuote(quoteId)
    val quoteInterventionZones = MutableLiveData<MutableList<QuoteInterventionZone>>()

    private val _navigateToQuoteList = MutableLiveData<Boolean>()
    val navigateToQuoteList: LiveData<Boolean>
        get() = _navigateToQuoteList

    fun onSelectClient(clientName: String) {
        selectedClient.value = _clients.value?.first { it.name == clientName }
    }

    fun onSelectInterventionZone(interventionZoneName: String) {
        selectedInterventionZone.value = _clientInterventionZones.value?.first { it.name == interventionZoneName }
    }

    fun addQuoteInterventionZone() {
        selectedInterventionZone.value?.let {
            val quoteInterventionZones = this.quoteInterventionZones.value ?: mutableListOf()

            if (interventionZoneNotAlreadyAdded(quoteInterventionZones, it)) {
                quoteInterventionZones.add(QuoteInterventionZone.from(it))
                this.quoteInterventionZones.value = quoteInterventionZones
            }
        }
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

    private fun interventionZoneNotAlreadyAdded(
        quoteInterventionZones: MutableList<QuoteInterventionZone>,
        interventionZone: InterventionZone
    ) = quoteInterventionZones.none { intervention -> intervention.interventionZoneId == interventionZone.interventionZoneId }

    private fun saveQuote() {
        val client = selectedClient.value
        val editedQuote = quote.value

        if (client != null && editedQuote != null) {
            editedQuote.clientId = client.clientId

            val areAllInterventionZonesValid = quoteInterventionZones.value?.distinctBy { it.interventionZoneName }?.size == quoteInterventionZones.value?.size ?: true
            if (areAllInterventionZonesValid) {
                val updatedInterventionZones = quoteInterventionZones.value ?: mutableListOf()
                quoteRepository.saveQuoteInterventionZones(editedQuote, updatedInterventionZones)
            }
        }
    }

    private fun selectClientInterventionZones(): LiveData<List<InterventionZone>> {
        return Transformations.switchMap(selectedClient) {
            if (it == null) {
                MutableLiveData()
            } else {
                clientRepository.getClientInterventionZones(it.clientId)
            }
        }
    }

    private fun selectClientNames(): LiveData<List<String>> {
        return Transformations.map(_clients) {
            it?.map { client -> client.name } ?: listOf()
        }
    }

    private fun selectInterventionZoneNames(): LiveData<List<String>> {
        return Transformations.map(_clientInterventionZones) {
            it?.map { interventionZone -> interventionZone.name } ?: listOf()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
