package com.persival.realestatemanagerkotlin.domain.permissions

import android.app.Activity
import androidx.lifecycle.LiveData

interface PermissionRepository {

    fun refreshLocationPermission()

    fun refreshGpsActivation()

    fun isLocationPermission(): LiveData<Boolean>

    fun isGpsActivated(): LiveData<Boolean>

    fun requestStoragePermission(activity: Activity)
}
