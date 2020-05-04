package am.falconry.client

import am.falconry.database.client.ClientRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClientViewModelFactory(
    private val clientId: Long,
    private val repository: ClientRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientViewModel::class.java)) {
            return ClientViewModel(clientId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}