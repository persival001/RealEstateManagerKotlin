package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import com.persival.realestatemanagerkotlin.domain.property.model.Property
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model.PropertyWithPhotosAndPOI
import kotlinx.coroutines.flow.Flow

interface LocalRepository {

    suspend fun insertProperty(property: Property): Long?

    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest): Long?

    suspend fun insertPhoto(photo: Photo): Long?

    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOI>>

    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOI>

    fun getAllPropertiesLatLng(): Flow<List<String>>

    fun getPropertyPhotos(propertyId: Long): Flow<List<Photo>>

    fun getSearchedPropertiesWithPOIs(
        type: String?,
        minPrice: Int?,
        maxPrice: Int?,
        minArea: Int?,
        maxArea: Int?,
        isSold: Boolean?,
        timeFilter: String?,
        poiSchool: String?,
        poiRestaurant: String?,
        poiPublicTransport: String?,
        poiHospital: String?,
        poiStore: String?,
        poiGreenSpaces: String?,

        ): Flow<List<PropertyWithPhotosAndPOI>>

    suspend fun updateProperty(property: Property): Int

    suspend fun updatePhoto(photo: Photo): Int

    suspend fun updatePointOfInterest(pointOfInterest: PointOfInterest): Int

    suspend fun updatePointOfInterestWithPropertyId(
        propertyId: Long,
        pointOfInterestEntities: List<PointOfInterest>
    )

    suspend fun deletePhotoByPropertyIdAndPhotoId(propertyId: Long, photoId: Long)

}
