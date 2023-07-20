package com.persival.realestatemanagerkotlin.domain.location

import android.location.Location
import androidx.lifecycle.LiveData

interface LocationRepository {
    fun getCurrentLocation(): LiveData<Location>
}