package com.persival.realestatemanagerkotlin.data.synchronize_database

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SynchronizeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var dataSyncRepository: DataSyncRepository

    override suspend fun doWork(): Result {
        try {
            dataSyncRepository.synchronizeData()
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val syncRequest = PeriodicWorkRequestBuilder<SynchronizeWorker>(1, TimeUnit.HOURS)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueue(syncRequest)
}