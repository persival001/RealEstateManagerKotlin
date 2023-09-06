package com.persival.realestatemanagerkotlin.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.LatLng
import com.persival.realestatemanagerkotlin.domain.location.GetLocationUseCase
import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import com.persival.realestatemanagerkotlin.domain.permissions.IsGpsActivatedUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshGpsActivationUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshLocationPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class
MapViewModel @Inject constructor(
    private val getAllPropertiesWithPhotosAndPOIUseCase: GetAllPropertiesWithPhotosAndPOIUseCase,
    getLocationUseCase: GetLocationUseCase,
    private val setSelectedPropertyIdUseCase: SetSelectedPropertyIdUseCase,
    private val refreshGpsActivationUseCase: RefreshGpsActivationUseCase,
    private val refreshLocationPermissionUseCase: RefreshLocationPermissionUseCase,
    private val isGpsActivatedUseCase: IsGpsActivatedUseCase,
) : ViewModel() {

    val locationUpdates: LiveData<LocationEntity> = getLocationUseCase.invoke().asLiveData()

    val propertiesLatLngWithId: Flow<List<MapViewState>> = flow {
        getAllPropertiesWithPhotosAndPOIUseCase.invoke().collect { allProperties ->
            val mapViewStateList = allProperties.map { property ->
                val parts = property.property.latLng.split(",")
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                MapViewState(
                    latLng = LatLng(latitude, longitude),
                    id = property.property.id,
                    address = property.property.address
                )
            }

            emit(mapViewStateList)
        }
    }

    fun isGpsActivatedLiveData(): LiveData<Boolean> = isGpsActivatedUseCase.invoke()

    fun refreshGpsActivation() {
        refreshGpsActivationUseCase.invoke()
    }

    fun refreshLocationPermission() {
        refreshLocationPermissionUseCase.invoke()
    }

    fun updateSelectedPropertyId(id: Long?) {
        setSelectedPropertyIdUseCase(id)
    }

}