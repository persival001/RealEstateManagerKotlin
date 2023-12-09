package com.persival.realestatemanagerkotlin.domain.location

import com.persival.realestatemanagerkotlin.domain.location.model.Location
import com.persival.realestatemanagerkotlin.domain.permissions.HasLocationPermissionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLocationUseCase @Inject constructor(
    private val hasLocationPermissionUseCase: HasLocationPermissionUseCase,
    private val locationRepository: LocationRepository,
) {
    fun invoke(): Flow<Location> = hasLocationPermissionUseCase.invoke()
        .flatMapLatest { hasPermission: Boolean ->
            if (hasPermission) {
                locationRepository.getLocationFlow()
            } else {
                emptyFlow()
            }
        }

}
