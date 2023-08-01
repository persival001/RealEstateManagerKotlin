package com.persival.realestatemanagerkotlin.domain.permissions

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsLocationPermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    operator fun invoke(): StateFlow<Boolean> {
        return permissionRepository.locationPermissionFlow
    }
}