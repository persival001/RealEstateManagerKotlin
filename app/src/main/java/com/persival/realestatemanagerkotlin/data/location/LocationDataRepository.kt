package com.persival.realestatemanagerkotlin.data.location

import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.persival.realestatemanagerkotlin.domain.location.LocationRepository
import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDataRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationRepository {

    companion object {
        private const val SMALLEST_DISPLACEMENT_THRESHOLD_METER = 250
        private const val INTERVAL = 10000L
        private const val FASTEST_INTERVAL = INTERVAL / 2
    }

    private val _locationFlow = MutableStateFlow<LocationEntity?>(null)
    private val currentLocationFlow: StateFlow<LocationEntity?> get() = _locationFlow.asStateFlow()
    private var callback: LocationCallback? = null

    override fun getLocationFlow(): StateFlow<LocationEntity?> = currentLocationFlow

    @RequiresPermission(
        anyOf = [
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
        ]
    )
    override fun startLocationRequest() {
        if (callback == null) {
            callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val locationEntity = LocationEntity(
                            location.latitude,
                            location.longitude
                        )
                        _locationFlow.tryEmit(locationEntity)
                    }
                }
            }
        }

        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = INTERVAL
            fastestInterval = FASTEST_INTERVAL
            smallestDisplacement = SMALLEST_DISPLACEMENT_THRESHOLD_METER.toFloat()
        }

        val localCallback = callback
        if (localCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(localCallback)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, localCallback, Looper.getMainLooper())
        }
    }

    override fun stopLocationRequest() {
        callback?.let { fusedLocationProviderClient.removeLocationUpdates(it) }
    }
}