package com.persival.realestatemanagerkotlin.domain.conversion

import kotlinx.coroutines.flow.Flow

interface SharedPreferencesRepository {

    fun getCurrencyConversion(): Flow<Boolean>

    suspend fun getDateConversion(): Boolean

    suspend fun setCurrencyConversion(isActivated: Boolean)

    suspend fun setDateConversion(isActivated: Boolean)
}

