package com.persival.realestatemanagerkotlin.domain.permissions

import kotlinx.coroutines.flow.Flow

interface PermissionRepository {

    fun isLocationPermission(): Flow<Boolean>

    fun refreshLocationPermission()

    fun isGpsActivated(): Flow<Boolean>

    fun refreshGpsActivation()

    fun isCameraPermission(): Flow<Boolean>

    fun refreshCameraPermission()

    fun isStorageReadImagesPermission(): Flow<Boolean>

    fun refreshStorageReadImagesPermission()

    fun isStorageReadVideoPermission(): Flow<Boolean>

    fun refreshStorageReadVideoPermission()
}


