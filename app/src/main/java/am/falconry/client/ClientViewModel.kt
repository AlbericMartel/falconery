package am.falconry.client

import am.falconry.database.client.Client
import am.falconry.database.client.ClientDatabaseDao
import am.falconry.database.client.Location
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

    var client = MutableLiveData<Client>()

    var editableLocations = mutableListOf<Location>()
    var locations = MutableLiveData<List<Location>>(editableLocations)

    private val _navigateToClientList = MutableLiveData<Boolean>()
    val navigateToClientList: LiveData<Boolean>
        get() = _navigateToClientList

    init {
        uiScope.launch {
            client.value = loadClient(clientId)
            editableLocations = loadLocations(clientId)
        }
    }

    private suspend fun loadClient(clientId: Long): Client {
        return withContext(Dispatchers.IO) {
            database.getClient(clientId) ?: Client()
        }
    }

    private suspend fun loadLocations(clientId: Long): MutableList<Location> {
        return withContext(Dispatchers.IO) {
            database.getAllClientLocations(clientId).toMutableList()
        }
    }

    fun newClientLocation() {
        editableLocations.add(Location()).also {
            locations.value = editableLocations
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
        editableLocations.forEach {
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

    private fun isLocationValid(location: Location): Boolean {
        return location.name.isNotBlank()
    }

    fun doneNavigatingToClientList() {
        _navigateToClientList.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}