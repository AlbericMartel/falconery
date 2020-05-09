package am.falconry.client

import am.falconry.clientinterventionzone.InterventionZoneParams
import am.falconry.database.client.ClientRepository
import am.falconry.database.quote.QuoteRepository
import am.falconry.domain.InterventionZone
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class ClientViewModel(
    private val clientId: Long = 0L,
    private val clientRepository: ClientRepository,
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var client = clientRepository.getClient(clientId)
    var loadedInterventionZones: LiveData<List<InterventionZone>> = clientRepository.getClientInterventionZones(clientId)

    private val _goToClientList = MutableLiveData<Boolean>()
    val goToClientList: LiveData<Boolean>
        get() = _goToClientList

    private val _goToInterventionZone = MutableLiveData<InterventionZoneParams>()
    val goToInterventionZone: LiveData<InterventionZoneParams>
        get() = _goToInterventionZone

    fun newClientInterventionZone() {
        trySaveClientAndNavigateToInterventionZone()
    }

    fun trySaveClient() {
        if (isClientValid()) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    saveClient()
                }
            }
            _goToClientList.value = true
        }
    }

    fun createNewQuote(interventionZoneId: Long) {

    }

    private fun saveClient(): Long {
        return clientRepository.saveClient(client.value!!)
    }

    private fun isClientValid(): Boolean {
        with(client.value) {
            if (this != null) {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches() && name.isNotBlank()
            }
        }

        return false
    }

    private fun trySaveClientAndNavigateToInterventionZone() {
        if (isClientValid()) {
            uiScope.launch {
                val clientId = withContext(Dispatchers.IO) {
                    saveClient()
                }
                _goToInterventionZone.value = InterventionZoneParams(clientId, 0L)
            }
        }
    }

    fun goToInterventionZone(interventionZoneId: Long) {
        _goToInterventionZone.value = InterventionZoneParams(clientId, interventionZoneId)
    }

    fun doneGoToInterventionZone() {
        _goToInterventionZone.value = null
    }

    fun doneGoToClientList() {
        _goToClientList.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}