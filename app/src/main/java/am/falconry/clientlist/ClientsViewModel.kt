package am.falconry.clientlist

import am.falconry.database.client.ClientRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class ClientsViewModel(
    repository: ClientRepository,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val clients = repository.getAllClients()

    private val _navigateToClient = MutableLiveData<Long>()
    val navigateToClient: LiveData<Long>
        get() = _navigateToClient

    fun onNewClient() {
        _navigateToClient.value = 0L
    }

    fun onClientClicked(id: Long) {
        _navigateToClient.value = id
    }

    fun onClientDetailNavigated() {
        _navigateToClient.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}