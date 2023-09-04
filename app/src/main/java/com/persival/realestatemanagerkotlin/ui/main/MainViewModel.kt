package com.persival.realestatemanagerkotlin.ui.main

import androidx.lifecycle.ViewModel
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
) : ViewModel() {

    private var isTablet: Boolean = false

    fun getPropertyId() = getSelectedPropertyIdUseCase()

    fun onResume(isTablet: Boolean) {
        this.isTablet = isTablet
    }
}