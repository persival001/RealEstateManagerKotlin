package com.persival.realestatemanagerkotlin.domain.conversion

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsDateConversionButtonTriggeredUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {
    suspend fun invoke(isActivated: Boolean) {
        sharedPreferencesRepository.setDateConversion(isActivated)
    }
}