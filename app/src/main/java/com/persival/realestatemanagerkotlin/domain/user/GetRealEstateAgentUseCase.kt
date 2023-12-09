package com.persival.realestatemanagerkotlin.domain.user

import com.persival.realestatemanagerkotlin.domain.user.model.RealEstateAgent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealEstateAgentUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
) {
    fun invoke(): RealEstateAgent? = firebaseRepository.getRealEstateAgentIdentity()
}
