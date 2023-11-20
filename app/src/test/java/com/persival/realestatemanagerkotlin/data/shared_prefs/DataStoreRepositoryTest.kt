package com.persival.realestatemanagerkotlin.data.shared_prefs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DataStoreRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataStoreRepository: DataStoreRepository

    companion object {
        val KEY_CURRENCY = booleanPreferencesKey("key_currency")
        val KEY_DATE = booleanPreferencesKey("key_date")
    }

    @Before
    fun setup() {

        // Mock DataStore
        dataStore = mockk()

        // Mock DataStore data flow
        coEvery { dataStore.data } returns flowOf(emptyPreferences())
    }

    @Test
    fun setCurrencyConversion_SetsValueCorrectly() = testCoroutineRule.runTest {
        val expected = true

        // Mock the edit function
        coEvery { dataStore.edit(any()) } coAnswers {
            val editBlock = arg<MutablePreferences.() -> Unit>(0)
            // Create a new MutablePreferences from emptyPreferences
            val mutablePreferences = emptyPreferences().toMutablePreferences()
            editBlock(mutablePreferences)
            // Return the new Preferences after the edit block has been applied
            mutablePreferences.toPreferences()
        }

        // Act: Attempt to set the currency conversion preference
        dataStoreRepository.setCurrencyConversion(expected)

        // Assert: Verify that the preference was set correctly
        val storedPreferences = dataStore.data.first() // Suspend function that needs to be awaited
        assertTrue(storedPreferences[DataStoreRepository.KEY_CURRENCY] == expected)
    }

    @Test
    fun setDateConversion_SetsValueCorrectly() = testCoroutineRule.runTest {
        val expected = true

        // Mock the edit function
        coEvery { dataStore.edit(any()) } coAnswers {
            val editBlock = arg<MutablePreferences.() -> Unit>(0)
            // Create a new MutablePreferences from emptyPreferences
            val mutablePreferences = emptyPreferences().toMutablePreferences()
            editBlock(mutablePreferences)
            // Return the new Preferences after the edit block has been applied
            mutablePreferences.toPreferences()
        }

        // Set the date setting
        dataStoreRepository.setDateConversion(expected)

        // Verify that edit was called on the DataStore with correct value
        coVerify { dataStore.edit(any()) }

        // Assert: Verify that the preference was set correctly
        val storedPreferences = dataStore.data.first() // Suspend function that needs to be awaited
        assertTrue(storedPreferences[DataStoreRepository.KEY_DATE] == expected)
    }

    @Test
    fun getCurrencyConversion_ReturnsFalseByDefault() = testCoroutineRule.runTest {
        // Assert the default value when preferences are empty
        assertFalse(dataStoreRepository.isEuroConversionEnabled().first())
    }

    @Test
    fun getDateConversion_ReturnsFalseByDefault() = testCoroutineRule.runTest {
        // Assert the default value when preferences are empty
        assertFalse(dataStoreRepository.getDateConversion())
    }

    @Test
    fun `should save and retrieve currency conversion setting`() = testCoroutineRule.runTest {
        val expected = true
        dataStoreRepository.setCurrencyConversion(expected)
        val actual = dataStore.data.first()[DataStoreRepository.KEY_CURRENCY]
        assertEquals(expected, actual)
    }

    @Test
    fun `getCurrencyConversion emits correct value`() = testCoroutineRule.runTest {
        val testValue = true

        // Act - Set the currency conversion setting
        dataStoreRepository.setCurrencyConversion(testValue)

        // Assert - Confirm that the emitted value is the test value
        val emittedValue = dataStoreRepository.isEuroConversionEnabled().first()
        assertEquals(testValue, emittedValue)
    }

    @Test
    fun `setCurrencyConversion saves value correctly`() = testCoroutineRule.runTest {
        val testValue = true

        // Act - Save the currency conversion setting
        dataStoreRepository.setCurrencyConversion(testValue)

        // Confirm that the saved value is the test value
        dataStore.data.map { preferences ->
            assertEquals(testValue, preferences[DataStoreRepository.KEY_CURRENCY])
        }.toList()
    }


    @Test
    fun `getDateConversion returns correct value`() = testCoroutineRule.runTest {
        // Define a test value
        val testValue = true

        // Act - Save the date conversion setting
        dataStoreRepository.setDateConversion(testValue)

        // Assert - Confirm that the returned value is the test value
        val returnedValue = dataStoreRepository.getDateConversion()
        assertEquals(testValue, returnedValue)
    }

    @Test
    fun `setDateConversion saves value correctly`() = testCoroutineRule.runTest {
        // Define a test value
        val testValue = true

        // Act - Save the date conversion setting
        dataStoreRepository.setDateConversion(testValue)

        // Confirm that the saved value is the test value
        dataStore.data.map { preferences ->
            assertEquals(testValue, preferences[DataStoreRepository.KEY_DATE])
        }.toList()
    }
}
