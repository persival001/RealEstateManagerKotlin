package com.persival.realestatemanagerkotlin.domain.permissions

import android.app.Activity

interface PermissionRepository {
    fun requestLocationPermission(activity: Activity)
    fun requestStoragePermission(activity: Activity)
}
