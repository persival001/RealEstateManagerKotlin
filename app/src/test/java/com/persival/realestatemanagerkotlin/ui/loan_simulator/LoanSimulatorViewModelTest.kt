package com.persival.realestatemanagerkotlin.ui.loan_simulator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.math.pow

class LoanSimulatorViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LoanSimulatorViewModel

    @Before
    fun setUp() {
        // Initial setup and initialization of ViewModel
        viewModel = LoanSimulatorViewModel()
    }

    @Test
    fun `calculateMonthlyPayment returns success with correct monthly payment`() {
        // Given
        val bring = 20000.0
        val rate = 5.0
        val duration = 240 // 20 years
        val propertyPrice = 120000.0

        // Mocking the observer
        val observer: Observer<LoanCalculationState> = mockk()
        viewModel.loanState.observeForever(observer)

        // When
        viewModel.calculateMonthlyPayment(bring, rate, duration, propertyPrice)

        // Then
        val loanAmount = propertyPrice - bring
        val monthlyRate = rate / 12 / 100
        val monthlyPayment =
            loanAmount * monthlyRate / (1 - (1 + monthlyRate).pow(-duration.toDouble()))

        verify { observer.onChanged(LoanCalculationState.Success(monthlyPayment)) }

        // Clean up
        viewModel.loanState.removeObserver(observer)
    }

    @Test
    fun `calculateMonthlyPayment with zero duration returns invalid duration state`() {
        // Given
        val bring = 20000.0
        val rate = 5.0
        val duration = 0 // Invalid duration
        val propertyPrice = 120000.0

        // Mocking the observer
        val observer: Observer<LoanCalculationState> = mockk()
        viewModel.loanState.observeForever(observer)

        // When
        viewModel.calculateMonthlyPayment(bring, rate, duration, propertyPrice)

        // Then
        verify { observer.onChanged(LoanCalculationState.InvalidDuration) }

        // Clean up
        viewModel.loanState.removeObserver(observer)
    }


}