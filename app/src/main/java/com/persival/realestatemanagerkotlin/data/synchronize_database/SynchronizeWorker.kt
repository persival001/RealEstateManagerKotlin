package com.persival.realestatemanagerkotlin.data.synchronize_database

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SynchronizeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataSyncRepository: DataSyncRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            dataSyncRepository.synchronizeData()
            Result.success()
        } catch (e: Exception) {
            // TODO: Log or handle the exception as needed
            Result.retry()
        }
    }
}
