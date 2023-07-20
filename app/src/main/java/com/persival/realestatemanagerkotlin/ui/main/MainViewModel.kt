package com.persival.realestatemanagerkotlin.ui.main

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.persival.realestatemanagerkotlin.domain.location.GetLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase
    ) : ViewModel() {
    val currentLocation: LiveData<Location> = getLocationUseCase.invoke()
}