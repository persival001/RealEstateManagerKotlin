package fr.delcey.openclassrooms_master_detail_mvvm.data.current_mail

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdRepository @Inject constructor() {
    private val currentPropertyIdMutableSharedFlow = MutableStateFlow<String?>(null)
    val currentPropertyIdChannel = Channel<String>()
    val currentMPropertyIdFlow: StateFlow<String?> = currentPropertyIdMutableSharedFlow.asStateFlow()

    fun setCurrentPropertyId(currentId: String) {
        currentPropertyIdMutableSharedFlow.value = currentId
        currentPropertyIdChannel.trySend(currentId)
    }
}