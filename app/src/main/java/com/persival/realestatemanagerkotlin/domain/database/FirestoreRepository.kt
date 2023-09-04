package com.persival.realestatemanagerkotlin.domain.database

import com.persival.realestatemanagerkotlin.data.local_database.model.PhotoDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PointOfInterestDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyDto

interface FirestoreRepository {
    suspend fun getAllProperties(): List<PropertyDto>
    fun getPOIs(id: Long): List<PointOfInterestDto>
    fun getPhotos(id: Long): List<PhotoDto>
}
