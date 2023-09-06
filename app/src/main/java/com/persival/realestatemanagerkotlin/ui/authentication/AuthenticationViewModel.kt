package com.persival.realestatemanagerkotlin.ui.authentication

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.persival.realestatemanagerkotlin.domain.database.SynchronizeDatabaseUseCase
import com.persival.realestatemanagerkotlin.domain.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val synchronizeDatabaseUseCase: SynchronizeDatabaseUseCase,
) : ViewModel() {

    fun getCurrentUser(): FirebaseUser? = getCurrentUserUseCase.invoke()

    fun synchronizeDatabase() {
        synchronizeDatabaseUseCase.invoke()
    }
}