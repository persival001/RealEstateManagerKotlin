package com.persival.realestatemanagerkotlin.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsCurrencyConversionButtonTriggeredUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsDateConversionButtonTriggeredUseCase
import com.persival.realestatemanagerkotlin.domain.conversion.IsEuroConversionEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val isCurrencyConversionButtonTriggeredUseCase: IsCurrencyConversionButtonTriggeredUseCase,
    private val isDateConversionButtonTriggeredUseCase: IsDateConversionButtonTriggeredUseCase,
    private val isEuroConversionEnabledUseCase: IsEuroConversionEnabledUseCase,
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase,
) : ViewModel() {

    private val _conversionButtonState = MutableStateFlow(false)
    val conversionButtonState: StateFlow<Boolean> get() = _conversionButtonState

    private val _dateButtonState = MutableLiveData<Boolean>()
    val dateButtonState: LiveData<Boolean> get() = _dateButtonState

    init {
        viewModelScope.launch {
            isEuroConversionEnabledUseCase.invoke().collect { value ->
                _conversionButtonState.emit(value)
            }
        }

        viewModelScope.launch {
            val state = getSavedStateForDateConversionButtonUseCase.invoke().first()
            _dateButtonState.postValue(state)
        }

    }

    fun isCurrencyConversionTriggered(isTriggered: Boolean) {
        viewModelScope.launch {
            isCurrencyConversionButtonTriggeredUseCase.invoke(isTriggered)
        }
    }

    fun isDateConversionTriggered(isTriggered: Boolean) {
        viewModelScope.launch {
            isDateConversionButtonTriggeredUseCase.invoke(isTriggered)
        }
    }
}
