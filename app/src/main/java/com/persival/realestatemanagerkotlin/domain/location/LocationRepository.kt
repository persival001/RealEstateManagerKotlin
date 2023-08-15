package com.persival.realestatemanagerkotlin.domain.location

import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {

    fun getLocationFlow(): StateFlow<LocationEntity?>

    fun startLocationRequest()

    fun stopLocationRequest()
}
