package com.persival.realestatemanagerkotlin.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.persival.realestatemanagerkotlin.domain.location.LocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationDataRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val userLocation = MutableLiveData<Location>()

    override fun getCurrentLocation(): LiveData<Location> = userLocation

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationListener: LocationListener =
        LocationListener { location -> userLocation.value = location }

    init {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000L,
                250f,
                locationListener
            )
        }
    }
}

