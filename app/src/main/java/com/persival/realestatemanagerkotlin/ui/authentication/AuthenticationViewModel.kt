package com.persival.realestatemanagerkotlin.ui.authentication

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.persival.realestatemanagerkotlin.domain.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    fun getCurrentUser(): FirebaseUser? {
        return getCurrentUserUseCase.invoke()
    }
}