package com.persival.realestatemanagerkotlin.domain.conversion

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class IsDateConversionButtonTriggeredUseCaseTest {

    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    private lateinit var isDateConversionButtonTriggeredUseCase: IsDateConversionButtonTriggeredUseCase

    @Before
    fun setUp() {
        sharedPreferencesRepository = mockk(relaxed = true)

        coEvery { sharedPreferencesRepository.setDateConversion(any()) } coAnswers {}

        isDateConversionButtonTriggeredUseCase = IsDateConversionButtonTriggeredUseCase(sharedPreferencesRepository)
    }

    @Test
    fun `invoke calls setDateConversion with correct parameter`() = runBlocking {
        val testActivated = true

        isDateConversionButtonTriggeredUseCase.invoke(testActivated)

        coVerify { sharedPreferencesRepository.setDateConversion(testActivated) }
    }

    @Test
    fun `invoke propagates exception when setDateConversion fails`() = runBlocking {
        val testActivated = true
        val exception = RuntimeException("Error setting date conversion")

        coEvery { sharedPreferencesRepository.setDateConversion(any()) } throws exception

        try {
            isDateConversionButtonTriggeredUseCase.invoke(testActivated)
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            assertEquals("Error setting date conversion", e.message)
        }
    }

}

