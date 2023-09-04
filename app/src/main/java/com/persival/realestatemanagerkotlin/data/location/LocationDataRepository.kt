package com.persival.realestatemanagerkotlin.data.location

import android.os.Looper
import android.util.Log
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
                    Log.d(
                        "LocationDataRepository",
                        "Received location update: Latitude=${location.latitude}, Longitude=${location.longitude}"
                    )
                    val locationEntity = LocationEntity(location.latitude, location.longitude)
                    locationMutableStateFlow.tryEmit(locationEntity)
                } ?: Log.w("LocationDataRepository", "Received location update is null.")
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
        Log.d("LocationDataRepository", "Starting location updates.")
        fusedLocationProviderClient.removeLocationUpdates(callback)
        fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), callback, Looper.getMainLooper())
            .addOnFailureListener { e ->
                Log.e("LocationDataRepository", "Failed to request location updates.", e)
            }
    }

    private fun createLocationRequest() = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = INTERVAL
        fastestInterval = FASTEST_INTERVAL
        smallestDisplacement = SMALLEST_DISPLACEMENT_THRESHOLD_METER.toFloat()
    }

    override fun stopLocationRequest() {
        Log.d("LocationDataRepository", "Stopping location updates.")
        fusedLocationProviderClient.removeLocationUpdates(callback)
    }
}