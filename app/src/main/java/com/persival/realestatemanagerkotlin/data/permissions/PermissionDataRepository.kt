package com.persival.realestatemanagerkotlin.data.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.persival.realestatemanagerkotlin.domain.permissions.PermissionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionDataRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : PermissionRepository {

    companion object {
        const val STORAGE_REQUEST_CODE = 1
    }

    private val locationPermissionLiveData = MutableLiveData<Boolean>()
    private val isGpsActivatedLiveData = MutableLiveData<Boolean>()

    // Location permission
    override fun isLocationPermission(): LiveData<Boolean> = locationPermissionLiveData

    override fun refreshLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        locationPermissionLiveData.value = hasPermission
    }

    // Gps enabled or not
    override fun isGpsActivated(): LiveData<Boolean> = isGpsActivatedLiveData

    override fun refreshGpsActivation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        isGpsActivatedLiveData.value = isGPSEnabled
    }

    // Storage permission
    override fun requestStoragePermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_REQUEST_CODE
        )
    }

}
