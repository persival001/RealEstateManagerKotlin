package com.persival.realestatemanagerkotlin.data.shared_prefs

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.persival.realestatemanagerkotlin.domain.conversion.SharedPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsRepository @Inject constructor(
    application: Application
) : SharedPreferencesRepository {

    companion object {
        private const val KEY_CURRENCY = "KEY_CURRENCY"
        private const val KEY_DATE = "KEY_DATE"
    }

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

    override fun getCurrencyConversion(): Boolean {
        return sharedPreferences.getBoolean(KEY_CURRENCY, false)
    }

    override fun setCurrencyConversion(isActivated: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_CURRENCY, isActivated)
            apply()
        }
        Log.d("SharedPrefsRepo", "Set currency conversion to: $isActivated")
    }

    override fun getDateConversion(): Boolean {
        return sharedPreferences.getBoolean(KEY_DATE, false)
    }

    override fun setDateConversion(isActivated: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_DATE, isActivated)
            apply()
        }
    }

}