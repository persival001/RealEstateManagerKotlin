package com.persival.realestatemanagerkotlin.domain.permissions

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshStoragePermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {

    fun invoke() {
        permissionRepository.refreshStorageReadImagesPermission()
    }
}