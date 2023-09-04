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

    companion object {
        private const val WORK_TAG = "SYNCHRONIZE_WORK"
    }

    private var isTablet: Boolean = false

    fun getPropertyId() = getSelectedPropertyIdUseCase()

    fun initializeWorkManager() {
        // Verify if the work is already running
        val workInfos = WorkManager.getInstance(application).getWorkInfosByTag(WORK_TAG).get() ?: return
        val shouldEnqueue = workInfos.none {
            it.state == WorkInfo.State.CANCELLED || it.state == WorkInfo.State.FAILED
        }

        if (shouldEnqueue) {
            WorkManager.getInstance(application).enqueue(
                PeriodicWorkRequestBuilder<SynchronizeWorker>(1, TimeUnit.HOURS)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .addTag(WORK_TAG)
                    .build()
            )
        }
    }

    fun onResume(isTablet: Boolean) {
        this.isTablet = isTablet
    }
}