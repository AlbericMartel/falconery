package am.falconry.client

import am.falconry.database.client.ClientRepository
import am.falconry.domain.InterventionZone
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class ClientViewModel(
    clientId: Long = 0L,
    private val repository: ClientRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var client = repository.getClient(clientId)
    var loadedInterventionZones: LiveData<List<InterventionZone>> = repository.getClientInterventionZones(clientId)

    private val _goToClientList = MutableLiveData<Boolean>()
    val goToClientList: LiveData<Boolean>
        get() = _goToClientList

    private val _goToNewInterventionZoneForClientId = MutableLiveData<Long>()
    val goToNewInterventionZoneForClientId: LiveData<Long>
        get() = _goToNewInterventionZoneForClientId

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

    fun trySaveClientAndNavigateToInterventionZone() {
        if (isClientValid()) {
            uiScope.launch {
                val clientId = withContext(Dispatchers.IO) {
                    saveClient()
                }
                _goToNewInterventionZoneForClientId.value = clientId
            }
        }
    }

    private fun saveClient(): Long {
        return repository.saveClient(client.value!!)
    }

    private fun isClientValid(): Boolean {
        with(client.value) {
            if (this != null) {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches() && name.isNotBlank()
            }
        }

        return false
    }

    fun doneGoToClientList() {
        _goToClientList.value = null
    }

    fun doneGoToNewInterventionZoneForClientId() {
        _goToNewInterventionZoneForClientId.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}