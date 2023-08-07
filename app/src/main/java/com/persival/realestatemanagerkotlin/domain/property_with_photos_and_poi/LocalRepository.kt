package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import kotlinx.coroutines.flow.Flow

interface LocalRepository {

    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>>

    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity>

    suspend fun insertProperty(propertyEntity: PropertyEntity): Long

    suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long

    suspend fun insertPhoto(photoEntity: PhotoEntity): Long

    suspend fun updateProperty(propertyEntity: PropertyEntity)

    suspend fun updatePointOfInterest(pointOfInterestEntity: PointOfInterestEntity)

    suspend fun updatePhoto(photoEntity: PhotoEntity)

    suspend fun updatePropertyWithPhotosAndPOIs(
        property: PropertyEntity,
        photos: List<PhotoEntity>,
        pois: List<PointOfInterestEntity>
    )

}
