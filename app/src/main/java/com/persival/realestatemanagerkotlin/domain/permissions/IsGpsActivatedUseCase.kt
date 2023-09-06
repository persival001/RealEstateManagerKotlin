package com.persival.realestatemanagerkotlin.domain.permissions

import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsGpsActivatedUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    fun invoke(): LiveData<Boolean> = permissionRepository.isGpsActivated()
}

