package com.persival.realestatemanagerkotlin.domain.conversion

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class IsDateConversionButtonTriggeredUseCaseTest {

    private val sharedPreferencesRepository: SharedPreferencesRepository = mockk()

    private lateinit var isDateConversionButtonTriggeredUseCase: IsDateConversionButtonTriggeredUseCase

    @Before
    fun setUp() {
        isDateConversionButtonTriggeredUseCase = IsDateConversionButtonTriggeredUseCase(sharedPreferencesRepository)
    }

    @Test
    fun `invoke calls setDateConversion with true when activated`() = runTest {

        isDateConversionButtonTriggeredUseCase.invoke(true)

        coVerify(exactly = 1) { sharedPreferencesRepository.setDateConversion(true) }
    }

    @Test
    fun `invoke calls setDateConversion with false when not activated`() = runTest {

        isDateConversionButtonTriggeredUseCase.invoke(false)

        coVerify(exactly = 1) { sharedPreferencesRepository.setDateConversion(false) }
    }
}
