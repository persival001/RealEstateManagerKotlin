package com.persival.realestatemanagerkotlin.domain.permissions

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshGpsActivationUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    operator fun invoke() {
        return permissionRepository.refreshGpsActivation()
    }
}
