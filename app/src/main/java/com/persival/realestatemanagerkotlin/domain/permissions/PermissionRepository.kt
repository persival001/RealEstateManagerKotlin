package com.persival.realestatemanagerkotlin.domain.permissions

import kotlinx.coroutines.flow.StateFlow

interface PermissionRepository {
    val locationPermissionFlow: StateFlow<Boolean>
    val gpsActivatedFlow: StateFlow<Boolean>

    fun refreshLocationPermission()
    fun refreshGpsActivation()
}
