package am.falconry.client

import am.falconry.database.client.ClientRepository
import am.falconry.domain.Location
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
    var loadedLocations: LiveData<List<Location>> = repository.getClientLocations(clientId)
    var locations = MutableLiveData<MutableList<Location>>()

    private val _navigateToClientList = MutableLiveData<Boolean>()
    val navigateToClientList: LiveData<Boolean>
        get() = _navigateToClientList

    fun newClientLocation() {
        val currentLocations: MutableList<Location>? = locations.value
        currentLocations?.add(Location.newLocation())?.also {
            locations.value = currentLocations
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

        val areAllLocationsValid = locations.value?.all { location -> isLocationValid(location) } ?: true
        if (areAllLocationsValid) {
            val updatedLocations = locations.value ?: mutableListOf()
            repository.saveClient(client.value!!, updatedLocations)
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

    private fun isLocationValid(location: Location): Boolean {
        return location.name.isNotBlank()
    }

    fun doneNavigatingToClientList() {
        _navigateToClientList.value = null
    }

    fun updateTrappingOption(locationId: Long, checked: Boolean) {
        locations.value?.first { it.locationId == locationId }.also { it?.trapping = checked }
    }

    fun updateScaringOption(locationId: Long, checked: Boolean) {
        locations.value?.first { it.locationId == locationId }.also { it?.scaring = checked }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}