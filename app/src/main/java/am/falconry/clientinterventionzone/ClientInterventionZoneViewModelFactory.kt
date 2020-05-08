package am.falconry.clientinterventionzone

import am.falconry.database.client.ClientRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClientInterventionZoneViewModelFactory(
    private val clientId: Long,
    private val interventionZoneId: Long,
    private val repository: ClientRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientInterventionZoneViewModel::class.java)) {
            return ClientInterventionZoneViewModel(clientId, interventionZoneId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}