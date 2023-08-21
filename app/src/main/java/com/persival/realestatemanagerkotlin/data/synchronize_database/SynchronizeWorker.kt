package com.persival.realestatemanagerkotlin.data.synchronize_database

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import javax.inject.Inject

class SynchronizeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var dataSyncRepository: DataSyncRepository

    override suspend fun doWork(): Result {
        return try {
            dataSyncRepository.synchronizeData()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}