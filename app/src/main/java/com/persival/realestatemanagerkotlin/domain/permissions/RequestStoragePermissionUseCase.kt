package com.persival.realestatemanagerkotlin.domain.permissions

import android.app.Activity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestStoragePermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    fun invoke(activity: Activity) {
        permissionRepository.requestStoragePermission(activity)
    }
}