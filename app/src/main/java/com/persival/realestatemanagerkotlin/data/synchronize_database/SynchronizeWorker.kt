package com.persival.realestatemanagerkotlin.data.synchronize_database

import android.app.Application
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SynchronizeWorker @AssistedInject constructor(
    @Assisted application: Application,
    @Assisted workerParams: WorkerParameters,
    private val dataSyncRepository: DataSyncRepository,
) : CoroutineWorker(application, workerParams) {

    override suspend fun doWork(): Result = try {
        dataSyncRepository.synchronizeData()
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}