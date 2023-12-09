package com.persival.realestatemanagerkotlin.domain.database

import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyEntity

interface FirestoreRepository {
    suspend fun getAllProperties(): List<PropertyEntity>
    fun getPOIs(id: Long): List<PointOfInterestEntity>
    fun getPhotos(id: Long): List<PhotoEntity>
}
