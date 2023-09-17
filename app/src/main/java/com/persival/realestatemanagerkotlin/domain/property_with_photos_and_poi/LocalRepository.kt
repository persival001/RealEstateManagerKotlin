package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import kotlinx.coroutines.flow.Flow

interface LocalRepository {

    suspend fun insertProperty(propertyEntity: PropertyEntity): Long?

    suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long?

    suspend fun insertPhoto(photoEntity: PhotoEntity): Long?

    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>>

    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity>

    fun getAllPropertiesLatLng(): Flow<List<String>>

    fun getPropertyPhotos(propertyId: Long): Flow<List<PhotoEntity>>

    suspend fun updateProperty(propertyEntity: PropertyEntity): Int

    suspend fun updatePhoto(photoEntity: PhotoEntity): Int

    suspend fun updatePointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Int

    suspend fun updatePointOfInterestWithPropertyId(
        propertyId: Long,
        pointOfInterestEntities: List<PointOfInterestEntity>
    )

}
