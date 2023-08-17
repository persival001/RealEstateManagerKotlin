package com.persival.realestatemanagerkotlin.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.persival.realestatemanagerkotlin.domain.location.GetLocationUseCase
import com.persival.realestatemanagerkotlin.domain.location.StartLocationUseCase
import com.persival.realestatemanagerkotlin.domain.location.StopLocationUseCase
import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import com.persival.realestatemanagerkotlin.domain.property.GetAllLatLngUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllLatLngUseCase: GetAllLatLngUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val startLocationUseCase: StartLocationUseCase,
    private val stopLocationUseCase: StopLocationUseCase,
) : ViewModel() {

    val currentLocation: StateFlow<LocationEntity?> = getLocationUseCase.invoke()

    private val _propertiesLatLng = MutableLiveData<List<MapViewState>>()
    val propertiesLatLng: LiveData<List<MapViewState>> = _propertiesLatLng

    fun getAllPropertiesLatLng() {
        viewModelScope.launch {
            getAllLatLngUseCase.invoke().collect { allLatLngStrings ->
                val mapViewStateList = allLatLngStrings.map { latLngString ->
                    val parts = latLngString.split(",")
                    val latitude = parts[0].toDouble()
                    val longitude = parts[1].toDouble()
                    Log.d("MapViewModel", "getAllPropertiesLatLng: $latitude, $longitude")
                    MapViewState(LatLng(latitude, longitude))
                }

                withContext(Dispatchers.Main) {
                    _propertiesLatLng.value = mapViewStateList
                }
            }
        }
    }

    fun stopLocation() {
        stopLocationUseCase.invoke()
    }

    fun onResume() {
        /*val hasGpsPermission = Boolean.TRUE == isLocationPermissionUseCase.invoke().getValue()
        if (hasGpsPermission) {
            startLocationUseCase.invoke()
        } else {
            stopLocationUseCase.invoke()
        }*/
    }
}