package com.persival.realestatemanagerkotlin.domain.permissions

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshLocationPermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {

    fun invoke() {
        permissionRepository.refreshLocationPermission()
    }
}