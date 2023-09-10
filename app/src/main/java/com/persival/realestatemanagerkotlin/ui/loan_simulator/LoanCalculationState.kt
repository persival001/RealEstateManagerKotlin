package com.persival.realestatemanagerkotlin.ui.loan_simulator

sealed class LoanCalculationState {
    data class Success(val monthlyPayment: Double) : LoanCalculationState()
    object InvalidDuration : LoanCalculationState()
    object Error : LoanCalculationState()
}