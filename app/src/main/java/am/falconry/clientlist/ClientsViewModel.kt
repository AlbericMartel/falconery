package am.falconry.clientlist

import am.falconry.database.client.ClientDatabaseDao
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class ClientsViewModel(
    val database: ClientDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val clients = database.getAllClients()

    private val _navigateToClient = MutableLiveData<Boolean>()
    val navigateToClient: LiveData<Boolean>
        get() = _navigateToClient

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}