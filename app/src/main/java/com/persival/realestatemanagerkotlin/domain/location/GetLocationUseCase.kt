package com.persival.realestatemanagerkotlin.domain.location

import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    fun invoke(): Flow<LocationEntity> = locationRepository.getLocationFlow()
}