package com.persival.realestatemanagerkotlin.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForCurrencyConversionButton
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsCurrencyConversionButtonTriggeredUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsDateConversionButtonTriggeredUseCase
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
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
    private val isDateConversionButtonTriggeredUseCase: IsDateConversionButtonTriggeredUseCase = mockk(relaxed = true)
    private val getSavedStateForCurrencyConversionButton: GetSavedStateForCurrencyConversionButton = mockk()
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase = mockk()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        // Initialisation des mocks
        clearMocks(isCurrencyConversionButtonTriggeredUseCase, isDateConversionButtonTriggeredUseCase)
        coEvery { isCurrencyConversionButtonTriggeredUseCase.invoke(true) } just Runs
        coEvery { isDateConversionButtonTriggeredUseCase.invoke(true) } just Runs
        coEvery { getSavedStateForCurrencyConversionButton.invoke() } returns flowOf(true)
        coEvery { getSavedStateForDateConversionButtonUseCase.invoke() } returns true

        viewModel = SettingsViewModel(
            isCurrencyConversionButtonTriggeredUseCase = isCurrencyConversionButtonTriggeredUseCase,
            isDateConversionButtonTriggeredUseCase = isDateConversionButtonTriggeredUseCase,
            getSavedStateForCurrencyConversionButton = getSavedStateForCurrencyConversionButton,
            getSavedStateForDateConversionButtonUseCase = getSavedStateForDateConversionButtonUseCase
        )
    }

    @Test
    fun `When ViewModel is initialized, it gets the saved state for buttons`() = testCoroutineRule.runTest {
        // Then
        coVerify { getSavedStateForCurrencyConversionButton.invoke() }
        coVerify { getSavedStateForDateConversionButtonUseCase.invoke() }
    }

    @Test
    fun `When isCurrencyConversionTriggered is called, it triggers the use case`() =
        testCoroutineRule.runTest {
            // Given
            val isTriggered = true

            // When
            viewModel.isCurrencyConversionTriggered(isTriggered)

            // Then
            coVerify { isCurrencyConversionButtonTriggeredUseCase.invoke(eq(true)) }
        }

    @Test
    fun `When isDateConversionTriggered is called, it triggers the use case`() =
        testCoroutineRule.runTest {
            // Given
            val isTriggered = true

            // When
            viewModel.isDateConversionTriggered(isTriggered)

            // Then
            coVerify { isDateConversionButtonTriggeredUseCase.invoke(true) }
        }

}
