package com.persival.realestatemanagerkotlin.data.location

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.persival.realestatemanagerkotlin.domain.location.LocationRepository
import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class LocationDataRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationRepository {

    companion object {
        private const val SMALLEST_DISPLACEMENT_THRESHOLD_METER = 250
        private val INTERVAL = 10.seconds
        private val FASTEST_INTERVAL = INTERVAL / 2
    }

    @SuppressLint("MissingPermission")
    override fun getLocationFlow(): Flow<LocationEntity> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d(
                        "LocationDataRepository",
                        "Received location update: Latitude=${location.latitude}, Longitude=${location.longitude}"
                    )
                    trySend(LocationEntity(location.latitude, location.longitude))
                } ?: Log.w("LocationDataRepository", "Received location update is null.")
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
                interval = INTERVAL.inWholeMilliseconds
                fastestInterval = FASTEST_INTERVAL.inWholeMilliseconds
                smallestDisplacement = SMALLEST_DISPLACEMENT_THRESHOLD_METER.toFloat()
            },
            Dispatchers.IO.asExecutor(),
            callback,
        )

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(callback)
        }
    }.flowOn(Dispatchers.IO)
}