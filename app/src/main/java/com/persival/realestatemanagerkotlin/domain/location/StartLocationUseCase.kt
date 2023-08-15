package com.persival.realestatemanagerkotlin.domain.location

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    fun invoke() {
        locationRepository.startLocationRequest()
    }
}
