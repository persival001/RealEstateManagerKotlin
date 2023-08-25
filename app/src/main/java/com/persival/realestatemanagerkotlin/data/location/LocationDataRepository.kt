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

    private val locationMutableStateFlow = MutableStateFlow<LocationEntity?>(null)
    override fun getLocationFlow(): StateFlow<LocationEntity?> = locationMutableStateFlow.asStateFlow()

    private val callback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val locationEntity = LocationEntity(location.latitude, location.longitude)
                    locationMutableStateFlow.tryEmit(locationEntity)
                }
            }
        }
    }

    @RequiresPermission(
        anyOf = [
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
        ]
    )
    override fun startLocationRequest() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
        fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), callback, Looper.getMainLooper())
    }

    private fun createLocationRequest() = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = INTERVAL
        fastestInterval = FASTEST_INTERVAL
        smallestDisplacement = SMALLEST_DISPLACEMENT_THRESHOLD_METER.toFloat()
    }

    override fun stopLocationRequest() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
    }
}