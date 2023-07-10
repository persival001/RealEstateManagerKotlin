package com.persival.realestatemanagerkotlin

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import com.persival.realestatemanagerkotlin.data.PermissionRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Application.ActivityLifecycleCallbacks {
    @Inject
    var permissionRepository: PermissionRepository? = null
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.setDescription(CHANNEL_DESCRIPTION)
            val notificationManager: NotificationManager? = getSystemService<NotificationManager>(
                NotificationManager::class.java
            )
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        permissionRepository?.refreshLocationPermission()
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    companion object {
        private const val CHANNEL_NAME = "Rem Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for Rem app"
        private const val CHANNEL_ID = "REM_CHANNEL_ID"
    }
}
