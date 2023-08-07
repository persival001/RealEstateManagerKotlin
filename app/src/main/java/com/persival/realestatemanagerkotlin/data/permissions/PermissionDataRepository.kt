package com.persival.realestatemanagerkotlin.data.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import com.persival.realestatemanagerkotlin.domain.permissions.PermissionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionDataRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : PermissionRepository {

    override fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
    }

    override fun requestStoragePermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_REQUEST_CODE
        )
    }

    companion object {
        const val LOCATION_REQUEST_CODE = 1
        const val STORAGE_REQUEST_CODE = 2
    }
}
