package com.persival.realestatemanagerkotlin.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.persival.realestatemanagerkotlin.domain.location.GetLocationUseCase
import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import com.persival.realestatemanagerkotlin.domain.permissions.IsGpsActivatedUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.IsLocationPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshGpsActivationUseCase
import com.persival.realestatemanagerkotlin.domain.property.GetAllLatLngUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class
MapViewModel @Inject constructor(
    private val getAllLatLngUseCase: GetAllLatLngUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val refreshGpsActivationUseCase: RefreshGpsActivationUseCase,
    private val isGpsActivatedUseCase: IsGpsActivatedUseCase,
) : ViewModel() {

    val propertiesLatLng: LiveData<List<MapViewState>> = liveData {
        getAllLatLngUseCase.invoke().collect { allLatLngStrings ->
            val mapViewStateList = allLatLngStrings.map { latLngString ->
                val parts = latLngString.split(",")
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                Log.d("MapViewModel", "getAllPropertiesLatLng: $latitude, $longitude")
                MapViewState(LatLng(latitude, longitude))
            }

            emit(mapViewStateList)
        }
    }

    fun getLocationLiveData(): LiveData<LocationEntity> = getLocationUseCase.invoke().asLiveData()

    fun isGpsActivatedLiveData(): LiveData<Boolean> = isGpsActivatedUseCase.invoke()

    fun refreshGpsActivation() {
        refreshGpsActivationUseCase.invoke()
    }

  // TODO Flatmap dans le domain instead (GetLocationUseCase) !
//    fun onResume() {
//        Log.d("MapViewModel", "onResume called in ViewModel")
//        val hasGpsPermission = true == isLocationPermissionUseCase.invoke().value
//        if (hasGpsPermission) {
//            startLocationUseCase.invoke()
//        } else {
//            stopLocationUseCase.invoke()
//        }
//    }

}