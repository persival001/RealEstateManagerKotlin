package com.persival.realestatemanagerkotlin.data.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.persival.realestatemanagerkotlin.domain.permissions.PermissionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionDataRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionRepository {
    private val _locationPermissionFlow = MutableStateFlow(false)
    override val locationPermissionFlow: StateFlow<Boolean> = _locationPermissionFlow

    private val _gpsActivatedFlow = MutableStateFlow(false)
    override val gpsActivatedFlow: StateFlow<Boolean> = _gpsActivatedFlow

    override fun refreshLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        _locationPermissionFlow.tryEmit(hasPermission)
    }

    override fun refreshGpsActivation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        _gpsActivatedFlow.tryEmit(isGPSEnabled)
    }
}
