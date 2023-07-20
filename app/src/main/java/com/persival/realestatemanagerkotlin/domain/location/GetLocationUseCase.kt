package com.persival.realestatemanagerkotlin.domain.location

import android.location.Location
import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    fun invoke(): LiveData<Location> = locationRepository.getCurrentLocation()
}