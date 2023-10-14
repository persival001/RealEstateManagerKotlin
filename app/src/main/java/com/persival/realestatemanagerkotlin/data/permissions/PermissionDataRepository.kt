package com.persival.realestatemanagerkotlin.data.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.persival.realestatemanagerkotlin.domain.permissions.PermissionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionDataRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : PermissionRepository {

    private val locationPermissionFlow = MutableStateFlow(false)
    private val isGpsActivatedLiveData = MutableStateFlow(true)
    private val cameraPermissionFlow = MutableStateFlow(false)
    private val storageReadImagesPermissionFlow = MutableStateFlow(false)
    private val storageReadVideoPermissionFlow = MutableStateFlow(false)

    // Location permission
    override fun isLocationPermission(): Flow<Boolean> = locationPermissionFlow.asStateFlow()

    override fun refreshLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        locationPermissionFlow.value = hasPermission
    }

    // Is GPS activated or not
    override fun isGpsActivated(): Flow<Boolean> = isGpsActivatedLiveData

    override fun refreshGpsActivation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        isGpsActivatedLiveData.value = isGPSEnabled
    }

    // Camera permission
    override fun isCameraPermission(): Flow<Boolean> = cameraPermissionFlow.asStateFlow()

    override fun refreshCameraPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        cameraPermissionFlow.value = hasPermission
    }

    // Storage Read Images permission
    override fun isStorageReadImagesPermission(): Flow<Boolean> = storageReadImagesPermissionFlow.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun refreshStorageReadImagesPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
        storageReadImagesPermissionFlow.value = hasPermission
    }

    // Storage Read Video permission
    override fun isStorageReadVideoPermission(): Flow<Boolean> = storageReadVideoPermissionFlow.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun refreshStorageReadVideoPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_MEDIA_VIDEO
        ) == PackageManager.PERMISSION_GRANTED
        storageReadVideoPermissionFlow.value = hasPermission
    }

}
