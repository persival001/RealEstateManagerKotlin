package com.persival.realestatemanagerkotlin.ui.authentication

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.persival.realestatemanagerkotlin.domain.database.SynchronizeDatabaseUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RequestStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val requestStoragePermissionUseCase: RequestStoragePermissionUseCase,
    private val synchronizeDatabaseUseCase: SynchronizeDatabaseUseCase,
) : ViewModel() {

    fun getCurrentUser(): FirebaseUser? {
        return getCurrentUserUseCase.invoke()
    }

    fun onStorageButtonClicked(activity: Activity) {
        requestStoragePermission(activity)
    }

    private fun requestStoragePermission(activity: Activity) {
        requestStoragePermissionUseCase.invoke(activity)
    }

    fun synchronizeDatabase() {
        synchronizeDatabaseUseCase.invoke()
    }
}