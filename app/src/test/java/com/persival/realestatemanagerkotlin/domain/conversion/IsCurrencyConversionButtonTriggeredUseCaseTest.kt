package com.persival.realestatemanagerkotlin.domain.conversion

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class IsCurrencyConversionButtonTriggeredUseCaseTest {

    private val sharedPreferencesRepository: SharedPreferencesRepository = mockk()

    private lateinit var isCurrencyConversionButtonTriggeredUseCase: IsCurrencyConversionButtonTriggeredUseCase

    @Before
    fun setUp() {

        isCurrencyConversionButtonTriggeredUseCase =
            IsCurrencyConversionButtonTriggeredUseCase(sharedPreferencesRepository)
    }

    @Test
    fun `invoke calls setCurrencyConversion with true when activated`() = runTest {

        isCurrencyConversionButtonTriggeredUseCase.invoke(true)

        coVerify(exactly = 1) { sharedPreferencesRepository.setCurrencyConversion(true) }
    }

    @Test
    fun `invoke calls setCurrencyConversion with false when not activated`() = runTest {

        isCurrencyConversionButtonTriggeredUseCase.invoke(false)

        coVerify(exactly = 1) { sharedPreferencesRepository.setCurrencyConversion(false) }
    }
}
