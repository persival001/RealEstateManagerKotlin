package com.persival.realestatemanagerkotlin.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.persival.realestatemanagerkotlin.data.synchronize_database.SynchronizeWorker
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
) : ViewModel() {

    private var isTablet: Boolean = false

    fun getPropertyId() = getSelectedPropertyIdUseCase()

    fun initializeWorkManager() {
        val workTag = "SYNCHRONIZE_WORK"

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SynchronizeWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .addTag(workTag)
            .build()

        // Verify if the work is already running
        val workInfos = WorkManager.getInstance(application).getWorkInfosByTag(workTag).get()

        if (workInfos == null || workInfos.isEmpty() || workInfos.any {
                it.state == WorkInfo.State.CANCELLED || it.state == WorkInfo.State.FAILED
            }) {
            WorkManager.getInstance(application).enqueue(syncRequest)
        }
    }

    fun onResume(isTablet: Boolean) {
        this.isTablet = isTablet
    }
}