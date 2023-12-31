package com.persival.realestatemanagerkotlin.domain.permissions

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsGpsActivatedUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    fun invoke(): Flow<Boolean> = permissionRepository.isGpsActivated()
}

