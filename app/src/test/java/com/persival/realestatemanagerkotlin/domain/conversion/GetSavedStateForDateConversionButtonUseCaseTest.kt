package com.persival.realestatemanagerkotlin.domain.conversion

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSavedStateForDateConversionButtonUseCaseTest {

    private val sharedPreferencesRepository: SharedPreferencesRepository = mockk()

    private lateinit var getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase

    @Before
    fun setUp() {
        getSavedStateForDateConversionButtonUseCase =
            GetSavedStateForDateConversionButtonUseCase(sharedPreferencesRepository)
    }

    @Test
    fun `invoke returns true when repository returns true`() = runTest {

        coEvery { sharedPreferencesRepository.getDateConversion() } returns true

        val result = getSavedStateForDateConversionButtonUseCase.invoke()

        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `invoke returns false when repository returns false`() = runTest {

        coEvery { sharedPreferencesRepository.getDateConversion() } returns false

        val result = getSavedStateForDateConversionButtonUseCase.invoke()

        assertThat(result).isEqualTo(false)
    }
}

