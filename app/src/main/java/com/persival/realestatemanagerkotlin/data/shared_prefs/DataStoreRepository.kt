package com.persival.realestatemanagerkotlin.data.shared_prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.persival.realestatemanagerkotlin.domain.conversion.SharedPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    @ApplicationContext context: Context
) : SharedPreferencesRepository {

    // Initialize DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")
    private val dataStore = context.dataStore

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
    override suspend fun getDateConversion(): Boolean {
        return try {
            dataStore.data.first { it.contains(KEY_DATE) }[KEY_DATE] ?: false
        } catch (e: Exception) {
            // Handle exception
            false
        }
    }

    // Save date conversion setting
    override suspend fun setDateConversion(isActivated: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_DATE] = isActivated
        }
    }
}