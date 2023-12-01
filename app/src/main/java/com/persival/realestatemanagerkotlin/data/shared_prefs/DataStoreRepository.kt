package com.persival.realestatemanagerkotlin.data.shared_prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.persival.realestatemanagerkotlin.domain.conversion.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SharedPreferencesRepository {

    companion object {
        val KEY_CURRENCY = booleanPreferencesKey("key_currency")
        val KEY_DATE = booleanPreferencesKey("key_date")
    }

    // Fetch currency conversion setting
    override fun isEuroConversionEnabled(): Flow<Boolean> {
        return try {
            dataStore.data.map { preferences ->
                preferences[KEY_CURRENCY] ?: false
            }
        } catch (e: Exception) {
            // Handle exception and emit some default or error state
            flowOf(false)
        }
    }

    // Save currency conversion setting
    override suspend fun setCurrencyConversion(isActivated: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_CURRENCY] = isActivated
        }
    }

    // Fetch date conversion setting
    override fun isDateConversion(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[KEY_DATE] ?: false
            }
    }

    // Save date conversion setting
    override suspend fun setDateConversion(isActivated: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_DATE] = isActivated
        }
    }
}