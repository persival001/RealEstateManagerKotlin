package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Transaction
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDatabaseRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val propertyDao: PropertyDao,
    private val photoDao: PhotoDao,
    private val pointOfInterestDao: PointOfInterestDao,
) : LocalRepository {

    override suspend fun insertProperty(propertyEntity: PropertyEntity): Long {
        return propertyDao.insert(propertyEntity)
    }

    override suspend fun insertPhoto(photoEntity: PhotoEntity): Long {
        return photoDao.insert(photoEntity)
    }

    override suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long {
        return pointOfInterestDao.insert(pointOfInterestEntity)
    }

    @Transaction
    override suspend fun updatePropertyWithPhotosAndPOIs(
        property: PropertyEntity,
        photos: List<PhotoEntity>,
        pois: List<PointOfInterestEntity>
    ) {
        propertyDao.update(property)
        property.id?.let { photoDao.deletePhotosByPropertyId(it) }
        photoDao.insertAll(photos)
        property.id?.let { pointOfInterestDao.deletePOIsByPropertyId(it) }
        pointOfInterestDao.insertAll(pois)
    }


    override suspend fun updateProperty(propertyEntity: PropertyEntity) {
        return propertyDao.update(propertyEntity)
    }

    override suspend fun updatePhoto(photoEntity: PhotoEntity) {
        return photoDao.update(photoEntity)
    }

    override suspend fun updatePointOfInterest(pointOfInterestEntity: PointOfInterestEntity) {
        return pointOfInterestDao.update(pointOfInterestEntity)
    }

    override fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>> =
        propertyDao
            .getAllProperties()
            .flowOn(coroutineDispatcherProvider.io)

    override fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity> =
        propertyDao
            .getPropertyById(propertyId)
            .flowOn(coroutineDispatcherProvider.io)

    override fun getAllPropertiesLatLng(): Flow<List<String>> =
        propertyDao
            .getAllLatLng()
            .flowOn(coroutineDispatcherProvider.io)
}