package com.persival.realestatemanagerkotlin.ui.settings

import androidx.lifecycle.ViewModel
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForCurrencyConversionButton
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsCurrencyConversionButtonTriggeredUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsDateConversionButtonTriggeredUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val isCurrencyConversionButtonTriggeredUseCase: IsCurrencyConversionButtonTriggeredUseCase,
    private val isDateConversionButtonTriggeredUseCase: IsDateConversionButtonTriggeredUseCase,
    private val getSavedStateForCurrencyConversionButton: GetSavedStateForCurrencyConversionButton,
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase,
) : ViewModel() {

    val conversionButtonState = {
        getSavedStateForCurrencyConversionButton.invoke()
    }

    val dateButtonState = {
        getSavedStateForDateConversionButtonUseCase.invoke()
    }

    fun isCurrencyConversionTriggered(isTriggered: Boolean) {
        isCurrencyConversionButtonTriggeredUseCase.invoke(isTriggered)
    }

    fun isDateConversionTriggered(isTriggered: Boolean) {
        isDateConversionButtonTriggeredUseCase.invoke(isTriggered)
    }

}