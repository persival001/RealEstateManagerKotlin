package com.persival.realestatemanagerkotlin.domain.conversion

interface SharedPreferencesRepository {

    fun getCurrencyConversion(): Boolean

    fun getDateConversion(): Boolean

    fun setCurrencyConversion(isActivated: Boolean)

    fun setDateConversion(isActivated: Boolean)

}
