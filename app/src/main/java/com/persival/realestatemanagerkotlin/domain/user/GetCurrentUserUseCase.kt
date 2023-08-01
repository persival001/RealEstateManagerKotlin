package com.persival.realestatemanagerkotlin.domain.user

import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun invoke(): FirebaseUser? {
        return firebaseRepository.getCurrentUser()
    }
}
