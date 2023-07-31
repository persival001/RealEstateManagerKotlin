package com.persival.realestatemanagerkotlin.domain.user

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLoggedUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun invoke(): LoggedUserEntity {
        return firebaseRepository.getLoggedUser()
    }
}
