package com.persival.realestatemanagerkotlin.domain.conversion

import androidx.lifecycle.LiveData

interface SharedPreferencesRepository {

    fun getCurrencyConversion(): Boolean

    fun getDateConversion(): LiveData<Boolean>

    fun setCurrencyConversion(isActivated: Boolean)

    fun setDateConversion(isActivated: Boolean)

}
