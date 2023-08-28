package com.persival.realestatemanagerkotlin.domain.conversion

import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSavedStateForDateConversionButtonUseCase @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) {

    fun invoke(): LiveData<Boolean> = sharedPreferencesRepository.getDateConversion()
}