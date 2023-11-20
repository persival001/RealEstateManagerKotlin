package com.persival.realestatemanagerkotlin.domain.conversion

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class IsEuroConversionEnabledUseCaseTest {

    private val sharedPreferencesRepository: SharedPreferencesRepository = mockk()

    private lateinit var isEuroConversionEnabledUseCase: IsEuroConversionEnabledUseCase

    @Before
    fun setUp() {
        isEuroConversionEnabledUseCase =
            IsEuroConversionEnabledUseCase(sharedPreferencesRepository)
    }

    @Test
    fun `invoke returns true when SharedPreferencesRepository returns Flow of true`() = runTest {
        // Mock the behaviour of SharedPreferencesRepository to return a Flow with true
        coEvery { sharedPreferencesRepository.isEuroConversionEnabled() } returns flowOf(true)

        // Execute the use case and get the result
        val result = isEuroConversionEnabledUseCase.invoke().first()

        // Assert that the result is true
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun `invoke returns false when SharedPreferencesRepository returns Flow of false`() = runTest {
        // Mock the behaviour of SharedPreferencesRepository to return a Flow with false
        coEvery { sharedPreferencesRepository.isEuroConversionEnabled() } returns flowOf(false)

        // Execute the use case and get the result
        val result = isEuroConversionEnabledUseCase.invoke().first()

        // Assert that the result is false
        assertThat(result).isEqualTo(false)
    }
}
