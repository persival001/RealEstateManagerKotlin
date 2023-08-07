package com.persival.realestatemanagerkotlin.ui.authentication

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.persival.realestatemanagerkotlin.domain.permissions.RequestLocationPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RequestStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val requestLocationPermissionUseCase: RequestLocationPermissionUseCase,
    private val requestStoragePermissionUseCase: RequestStoragePermissionUseCase,
) : ViewModel() {

    fun getCurrentUser(): FirebaseUser? {
        return getCurrentUserUseCase.invoke()
    }

    fun onLocationButtonClicked(activity: Activity) {
        requestLocationPermission(activity)
    }

    fun onStorageButtonClicked(activity: Activity) {
        requestStoragePermission(activity)
    }

    private fun requestLocationPermission(activity: Activity) {
        requestLocationPermissionUseCase.invoke(activity)
    }

    private fun requestStoragePermission(activity: Activity) {
        requestStoragePermissionUseCase.invoke(activity)
    }
}