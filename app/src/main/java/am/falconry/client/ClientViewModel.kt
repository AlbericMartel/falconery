package am.falconry.client

import am.falconry.database.client.ClientRepository
import am.falconry.domain.Client
import am.falconry.domain.Location
import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class ClientViewModel(
    private val clientId: Long = 0L,
    private val repository: ClientRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var client = MutableLiveData<Client>()
    var locations = MutableLiveData<List<Location>>()

    private val _navigateToClientList = MutableLiveData<Boolean>()
    val navigateToClientList: LiveData<Boolean>
        get() = _navigateToClientList

    init {
        uiScope.launch {
            client.value = loadClient(clientId)
            locations.value = loadLocations(clientId)
        }
    }

    private suspend fun loadClient(clientId: Long): Client {
        return withContext(Dispatchers.IO) {
            repository.getClient(clientId)
        }
    }

    private suspend fun loadLocations(clientId: Long): MutableList<Location> {
        return withContext(Dispatchers.IO) {
            repository.getClientLocations(clientId).toMutableList()
        }
    }

    fun newClientLocation() {
        val currentLocations: MutableList<Location>? = locations.value?.toMutableList()
        currentLocations?.add(repository.newLocation())?.also {
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

    fun cancel() {
        _navigateToClientList.value = true
    }

    private fun saveClient() {
        if (client.value == null) return

        val areAllLocationsValid = locations.value?.all { location -> isLocationValid(location) } ?: true
        if (areAllLocationsValid) {
            val updatedLocations = locations.value ?: listOf()
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