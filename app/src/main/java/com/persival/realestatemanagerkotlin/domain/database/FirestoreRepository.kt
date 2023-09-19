package com.persival.realestatemanagerkotlin.domain.database

import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDto
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDto
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDto

interface FirestoreRepository {
    suspend fun getAllProperties(): List<PropertyDto>
    fun getPOIs(id: Long): List<PointOfInterestDto>
    fun getPhotos(id: Long): List<PhotoDto>
}
