package com.persival.realestatemanagerkotlin.domain.conversion

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class IsCurrencyConversionButtonTriggeredUseCaseTest {

    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    private lateinit var isCurrencyConversionButtonTriggeredUseCase: IsCurrencyConversionButtonTriggeredUseCase

    @Before
    fun setUp() {
        sharedPreferencesRepository = mockk(relaxed = true)

        coEvery { sharedPreferencesRepository.setCurrencyConversion(any()) } coAnswers {}

        isCurrencyConversionButtonTriggeredUseCase =
            IsCurrencyConversionButtonTriggeredUseCase(sharedPreferencesRepository)
    }

    @Test
    fun `invoke calls setCurrencyConversion with correct parameter`() = runBlocking {
        val testActivated = true

        isCurrencyConversionButtonTriggeredUseCase.invoke(testActivated)

        coVerify { sharedPreferencesRepository.setCurrencyConversion(testActivated) }
    }
}
