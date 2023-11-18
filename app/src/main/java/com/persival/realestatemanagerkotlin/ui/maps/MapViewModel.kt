package com.persival.realestatemanagerkotlin.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.google.android.gms.maps.model.LatLng
import com.persival.realestatemanagerkotlin.domain.location.GetLocationUseCase
import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import com.persival.realestatemanagerkotlin.domain.permissions.IsGpsActivatedUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshGpsActivationUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshLocationPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
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
    getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
) : ViewModel() {

    val locationUpdates: LiveData<LocationEntity> = getLocationUseCase.invoke().asLiveData()

    val hasNullValues = MutableStateFlow(false)

    val propertiesLatLngWithId: Flow<List<MapViewState>> = flow {
        getAllPropertiesWithPhotosAndPOIUseCase.invoke().collect { allProperties ->
            var foundNull = false

            val mapViewStateList = allProperties.mapNotNull { property ->
                property.property.run {
                    val parts = latLng.split(",")
                    val latitude = parts.getOrNull(0)?.toDoubleOrNull()
                    val longitude = parts.getOrNull(1)?.toDoubleOrNull()

                    if (latitude != null && longitude != null) {
                        MapViewState(
                            latLng = LatLng(latitude, longitude),
                            id = id,
                            address = address
                        )
                    } else {
                        foundNull = true
                        null
                    }
                }
            }

            hasNullValues.emit(foundNull)
            emit(mapViewStateList)
        }
    }

    val getSelectedPropertyLatLng: LiveData<LatLng> = getSelectedPropertyIdUseCase.invoke()
        .asLiveData()
        .switchMap { selectedPropertyId ->
            liveData {
                val allProperties = getAllPropertiesWithPhotosAndPOIUseCase.invoke().firstOrNull()
                val selectedPropertyLatLng =
                    allProperties?.find { it.property.id == selectedPropertyId }?.property?.latLng
                selectedPropertyLatLng?.let {
                    emit(LatLng(it.split(",")[0].toDouble(), it.split(",")[1].toDouble()))
                }
            }
        }

    fun isGpsActivated(): Flow<Boolean> = isGpsActivatedUseCase.invoke()

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