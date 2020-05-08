package am.falconry.clientinterventionzone

import am.falconry.database.client.ClientRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClientInterventionZoneViewModelFactory(
    private val params: InterventionZoneParams,
    private val repository: ClientRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientInterventionZoneViewModel::class.java)) {
            return ClientInterventionZoneViewModel(params, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}