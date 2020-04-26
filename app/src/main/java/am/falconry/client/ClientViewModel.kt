package am.falconry.client

import am.falconry.database.client.ClientEntity
import am.falconry.database.client.ClientDatabaseDao
import am.falconry.database.client.LocationEntity
import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class ClientViewModel(
    private val clientId: Long = 0L,
    val database: ClientDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var client = MutableLiveData<ClientEntity>()
    var locations = MutableLiveData<List<LocationEntity>>()

    private val _navigateToClientList = MutableLiveData<Boolean>()
    val navigateToClientList: LiveData<Boolean>
        get() = _navigateToClientList

    init {
        uiScope.launch {
            client.value = loadClient(clientId)
            locations.value = loadLocations(clientId)
        }
    }

    private suspend fun loadClient(clientId: Long): ClientEntity {
        return withContext(Dispatchers.IO) {
            database.getClient(clientId) ?: ClientEntity()
        }
    }

    private suspend fun loadLocations(clientId: Long): MutableList<LocationEntity> {
        return withContext(Dispatchers.IO) {
            database.getAllClientLocations(clientId).toMutableList()
        }
    }

    fun newClientLocation() {
        val currentLocations: MutableList<LocationEntity>? = locations.value?.toMutableList()
        currentLocations?.add(LocationEntity())?.also {
            locations.value = currentLocations
        }
    }

    fun save() {
        if (isClientValid()) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    saveClientAndLocations()
                }
            }
            _navigateToClientList.value = true
        }
    }

    fun cancel() {
        _navigateToClientList.value = true
    }

    private fun saveClientAndLocations() {
        with(client.value) {
            this?.let {
                var clientId = it.clientId
                if (clientId != 0L) {
                    database.updateClient(it)
                } else {
                    clientId = database.insertClient(it)
                }

                saveOrUpdateClientLocations(clientId)
            }
        }
    }

    private fun saveOrUpdateClientLocations(clientId: Long) {
        locations.value?.forEach {
            if (isLocationValid(it)) {
                if (it.clientId != 0L) {
                    database.updateLocation(it)
                } else {
                    it.clientId = clientId
                    database.insertLocation(it)
                }
            }
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

    private fun isLocationValid(location: LocationEntity): Boolean {
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