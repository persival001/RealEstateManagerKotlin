package com.persival.realestatemanagerkotlin.domain.conversion

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSavedStateForDateConversionButtonUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    fun invoke() = sharedPreferencesRepository.getDateConversion()
}