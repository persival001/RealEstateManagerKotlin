package com.persival.realestatemanagerkotlin.domain.conversion

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsCurrencyConversionButtonTriggeredUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    fun invoke(isActivated: Boolean) {
        sharedPreferencesRepository.setCurrencyConversion(isActivated)
    }
}