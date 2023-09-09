package com.persival.realestatemanagerkotlin.data.synchronize_database

import android.content.Context
import android.util.Log
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

    companion object {
        private const val TAG = "SynchronizeWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            dataSyncRepository.synchronizeData()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error synchronizing data: ", e)
            Result.retry()
        }
    }
}