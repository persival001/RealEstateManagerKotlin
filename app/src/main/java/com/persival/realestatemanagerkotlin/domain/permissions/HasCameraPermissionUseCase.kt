package com.persival.realestatemanagerkotlin.domain.permissions

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HasCameraPermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {

    fun invoke(): Flow<Boolean> = permissionRepository.isCameraPermission()
}