package com.persival.realestatemanagerkotlin.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsCurrencyConversionButtonTriggeredUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsDateConversionButtonTriggeredUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsEuroConversionEnabledUseCase
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mocks
    private val isCurrencyConversionButtonTriggeredUseCase: IsCurrencyConversionButtonTriggeredUseCase = mockk()
    private val isDateConversionButtonTriggeredUseCase: IsDateConversionButtonTriggeredUseCase = mockk()
    private val isEuroConversionEnabledUseCase: IsEuroConversionEnabledUseCase =
        mockk()
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase = mockk()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        coJustRun { isCurrencyConversionButtonTriggeredUseCase.invoke(any()) }
        coJustRun { isDateConversionButtonTriggeredUseCase.invoke(any()) }
        coEvery { isEuroConversionEnabledUseCase.invoke() } returns flowOf(true)
        coEvery { getSavedStateForDateConversionButtonUseCase.invoke() } returns true

        viewModel = SettingsViewModel(
            isCurrencyConversionButtonTriggeredUseCase = isCurrencyConversionButtonTriggeredUseCase,
            isDateConversionButtonTriggeredUseCase = isDateConversionButtonTriggeredUseCase,
            isEuroConversionEnabledUseCase = isEuroConversionEnabledUseCase,
            getSavedStateForDateConversionButtonUseCase = getSavedStateForDateConversionButtonUseCase
        )
    }

    @Test
    fun `When ViewModel is initialized, it gets the saved state for buttons`() = testCoroutineRule.runTest {
        // Then
        coVerify { isEuroConversionEnabledUseCase.invoke() }
        coVerify { getSavedStateForDateConversionButtonUseCase.invoke() }
    }

    @Test
    fun `When isCurrencyConversionTriggered is called, it triggers the use case`() =
        testCoroutineRule.runTest {
            // Given
            val isTriggered = true

            // When
            viewModel.isCurrencyConversionTriggered(isTriggered)
            runCurrent()

            // Then
            coVerify { isCurrencyConversionButtonTriggeredUseCase.invoke(true) }
        }

    @Test
    fun `When isDateConversionTriggered is called, it triggers the use case`() =
        testCoroutineRule.runTest {
            // Given
            val isTriggered = true

            // When
            viewModel.isDateConversionTriggered(isTriggered)
            runCurrent()

            // Then
            coVerify { isDateConversionButtonTriggeredUseCase.invoke(true) }
        }

}
