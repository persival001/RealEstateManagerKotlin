package com.persival.realestatemanagerkotlin.data.local_database

import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.poi.PointOfInterestEntity
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
    private val pointOfInterestDao: PointOfInterestDao
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

    override fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>> =
        propertyDao
            .getAllProperties()
            .flowOn(coroutineDispatcherProvider.io)

    override fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity> =
        propertyDao
            .getPropertyById(propertyId)
            .flowOn(coroutineDispatcherProvider.io)
}

