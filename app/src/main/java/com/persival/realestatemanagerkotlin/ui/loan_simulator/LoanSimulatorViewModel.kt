package com.persival.realestatemanagerkotlin.ui.loan_simulator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class LoanSimulatorViewModel @Inject constructor(
) : ViewModel() {

    private val loanStateLiveData = MutableLiveData<LoanCalculationState>()
    val loanState: LiveData<LoanCalculationState> get() = loanStateLiveData

    fun calculateMonthlyPayment(bring: Double, rate: Double, duration: Int, propertyPrice: Double) {
        if (duration == 0) {
            loanStateLiveData.value = LoanCalculationState.InvalidDuration
            return
        }

        val loanAmount = propertyPrice - bring
        val monthlyRate = rate / 12 / 100
        val monthlyPayment = loanAmount * monthlyRate / (1 - (1 + monthlyRate).pow(-duration.toDouble()))

        loanStateLiveData.value = LoanCalculationState.Success(monthlyPayment)
    }
}

