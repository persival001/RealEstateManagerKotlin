package com.persival.realestatemanagerkotlin.domain.permissions

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsGpsActivatedUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    fun invoke(): StateFlow<Boolean> {
        return permissionRepository.gpsActivatedFlow
    }
}

