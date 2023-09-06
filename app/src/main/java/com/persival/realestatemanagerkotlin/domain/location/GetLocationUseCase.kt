package com.persival.realestatemanagerkotlin.domain.location

import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import com.persival.realestatemanagerkotlin.domain.permissions.IsLocationPermissionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationUseCase @Inject constructor(
    private val isLocationPermissionUseCase: IsLocationPermissionUseCase,
    private val locationRepository: LocationRepository,
) {
    fun invoke(): Flow<LocationEntity> = isLocationPermissionUseCase.invoke()
        .flatMapLatest { hasPermission: Boolean ->
            if (hasPermission) {
                locationRepository.getLocationFlow()
            } else {
                emptyFlow()
            }
        }

}
