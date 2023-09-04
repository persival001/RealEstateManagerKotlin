package com.persival.realestatemanagerkotlin.domain.location

import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocationFlow(): Flow<LocationEntity>
}
