package com.persival.realestatemanagerkotlin.domain.user

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealEstateAgentUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
) {
    fun invoke(): RealEstateAgentEntity? = firebaseRepository.getRealEstateAgentIdentity()
}
