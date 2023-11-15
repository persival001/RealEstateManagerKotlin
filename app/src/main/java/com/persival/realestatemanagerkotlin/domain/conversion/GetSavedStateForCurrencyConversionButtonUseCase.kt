package com.persival.realestatemanagerkotlin.domain.conversion

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSavedStateForCurrencyConversionButtonUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {
    fun invoke(): Flow<Boolean> = sharedPreferencesRepository.getCurrencyConversion()
}