package am.falconry.client.interventionzone

import am.falconry.database.client.ClientRepository
import am.falconry.domain.InterventionPoint
import am.falconry.domain.InterventionZone
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class ClientInterventionZoneViewModel(
    private val params: InterventionZoneParams,
    private val repository: ClientRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var interventionZone = loadInterventionZone()
    var loadedInterventionPoints: LiveData<List<InterventionPoint>> = repository.getZoneInterventionPoints(params.interventionZoneId)
    var interventionPoints = MutableLiveData<MutableList<InterventionPoint>>()

    private val _goToClient = MutableLiveData<Long>()
    val goToClient: LiveData<Long>
        get() = _goToClient

    fun newInterventionPoint() {
        val currentInterventionPoints: MutableList<InterventionPoint>? = interventionPoints.value
        currentInterventionPoints?.add(InterventionPoint.newInterventionPoint(params.interventionZoneId))?.also {
            interventionPoints.value = currentInterventionPoints
        }
    }

    private fun loadInterventionZone(): LiveData<InterventionZone> {
        return repository.getInterventionZoneByIdOrNewForClient(params.interventionZoneId, params.clientId)
    }

    fun trySaveInterventionZone() {
        if (isInterventionZoneValid()) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    saveInterventionZone()
                }
                _goToClient.value = params.clientId
            }
        }
    }

    private fun saveInterventionZone() {
        val areAllInterventionPointsValid = interventionPoints.value?.all { interventionPoint -> isInterventionPointValid(interventionPoint) } ?: true
        if (areAllInterventionPointsValid) {
            val updatedInterventionPoints = interventionPoints.value ?: mutableListOf()
            repository.saveInterventionZone(params.clientId, interventionZone.value!!, updatedInterventionPoints)
        }
    }

    private fun isInterventionZoneValid(): Boolean {
        with(interventionZone.value) {
            if (this != null) {
                return name.isNotBlank()
            }
        }

        return false
    }

    private fun isInterventionPointValid(interventionPoint: InterventionPoint): Boolean {
        return interventionPoint.name.isNotBlank()
    }

    fun doneGoToClient() {
        _goToClient.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}