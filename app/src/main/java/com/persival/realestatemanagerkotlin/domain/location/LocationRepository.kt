package com.persival.realestatemanagerkotlin.domain.location

import com.persival.realestatemanagerkotlin.domain.location.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocationFlow(): Flow<Location>
}
