package com.persival.realestatemanagerkotlin.domain.permissions

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

interface PermissionRepository {

    fun refreshLocationPermission()

    fun refreshGpsActivation()

    fun isLocationPermission(): Flow<Boolean>

    fun isGpsActivated(): LiveData<Boolean>
}
