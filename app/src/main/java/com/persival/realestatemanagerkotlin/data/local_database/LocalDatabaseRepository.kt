package com.persival.realestatemanagerkotlin.data.local_database

import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.poi.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDatabaseRepository @Inject constructor(
    private val propertyDao: PropertyDao,
    private val photoDao: PhotoDao,
    private val pointOfInterestDao: PointOfInterestDao
) : LocalRepository {

    suspend fun insertProperty(propertyEntity: PropertyEntity): Long {
        return propertyDao.insert(propertyEntity)
    }

    suspend fun insertPhoto(photoEntity: PhotoEntity): Long {
        return photoDao.insert(photoEntity)
    }

    suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long {
        return pointOfInterestDao.insert(pointOfInterestEntity)
    }

    private suspend fun getPropertyById(propertyId: Long): PropertyEntity {
        return propertyDao.getPropertyById(propertyId)
    }

    private suspend fun getPhotosByPropertyId(propertyId: Long): List<PhotoEntity> {
        return photoDao.getPhotosByPropertyId(propertyId)
    }

    private suspend fun getPoiByPropertyId(propertyId: Long): List<PointOfInterestEntity> {
        return pointOfInterestDao.getPOIsByPropertyId(propertyId)
    }

    override suspend fun getPropertyWithPhotosAndPOI(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity> =
        flow {
            val property = getPropertyById(propertyId)
            val photos = getPhotosByPropertyId(propertyId)
            val poi = getPoiByPropertyId(propertyId)
            emit(PropertyWithPhotosAndPOIEntity(property, photos, poi))
        }

}

