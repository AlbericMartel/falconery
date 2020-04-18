package am.falconry.client

import am.falconry.database.client.ClientDatabaseDao
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClientViewModelFactory(
    private val clientId: Long,
    private val dataSource: ClientDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientViewModel::class.java)) {
            return ClientViewModel(clientId, dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}