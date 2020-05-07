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
    var interventionZones = MutableLiveData<MutableList<InterventionZone>>()

    private val _navigateToClientList = MutableLiveData<Boolean>()
    val navigateToClientList: LiveData<Boolean>
        get() = _navigateToClientList

    fun newClientInterventionZone() {
        val currentInterventionZones: MutableList<InterventionZone>? = interventionZones.value
        currentInterventionZones?.add(InterventionZone.newInterventionZone())?.also {
            interventionZones.value = currentInterventionZones
        }
    }

    fun trySaveClient() {
        if (isClientValid()) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    saveClient()
                }
            }
            _navigateToClientList.value = true
        }
    }

    private fun saveClient() {
        if (client.value == null) return

        val areAllInterventionZonesValid = interventionZones.value?.all { interventionZone -> isInterventionZoneValid(interventionZone) } ?: true
        if (areAllInterventionZonesValid) {
            val updatedInterventionZones = interventionZones.value ?: mutableListOf()
            repository.saveClient(client.value!!, updatedInterventionZones)
        }
    }

    private fun isClientValid(): Boolean {
        with(client.value) {
            if (this != null) {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches() && name.isNotBlank()
            }
        }

        return false
    }

    private fun isInterventionZoneValid(interventionZone: InterventionZone): Boolean {
        return interventionZone.name.isNotBlank()
    }

    fun doneNavigatingToClientList() {
        _navigateToClientList.value = null
    }

    fun updateTrappingOption(interventionZoneId: Long, checked: Boolean) {
        interventionZones.value?.first { it.interventionZoneId == interventionZoneId }.also { it?.trapping = checked }
    }

    fun updateScaringOption(interventionZoneId: Long, checked: Boolean) {
        interventionZones.value?.first { it.interventionZoneId == interventionZoneId }.also { it?.scaring = checked }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}