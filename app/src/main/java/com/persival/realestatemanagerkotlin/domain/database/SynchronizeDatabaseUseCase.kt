package com.persival.realestatemanagerkotlin.domain.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SynchronizeDatabaseUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    fun invoke() {
        CoroutineScope(Dispatchers.IO).launch {
            syncRepository.synchronizeData()
        }
    }
}