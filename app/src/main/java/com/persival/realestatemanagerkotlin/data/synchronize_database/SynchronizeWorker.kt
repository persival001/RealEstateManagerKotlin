package com.persival.realestatemanagerkotlin.data.synchronize_database

import android.app.Application
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import javax.inject.Inject

class SynchronizeWorker @Inject constructor(
    application: Application,
    workerParams: WorkerParameters
) : CoroutineWorker(application, workerParams) {

    @Inject
    lateinit var dataSyncRepository: DataSyncRepository

    override suspend fun doWork(): Result = try {
        dataSyncRepository.synchronizeData()
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}