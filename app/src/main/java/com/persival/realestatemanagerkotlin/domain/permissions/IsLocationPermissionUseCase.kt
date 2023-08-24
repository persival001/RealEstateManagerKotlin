package com.persival.realestatemanagerkotlin.domain.permissions

import androidx.lifecycle.LiveData
import javax.inject.Inject

class IsLocationPermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {

    fun invoke(): LiveData<Boolean> = permissionRepository.isLocationPermission()
}