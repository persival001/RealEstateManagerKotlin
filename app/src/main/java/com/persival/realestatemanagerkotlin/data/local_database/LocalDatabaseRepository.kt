package com.persival.realestatemanagerkotlin.data.local_database

import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDatabaseRepository @Inject constructor(
    private val propertyDao: PropertyDao,
    private val photoDao: PhotoDao,
    private val pointOfInterestDao: PointOfInterestDao
) {

    suspend fun insertProperty(property: Property): Long {
        return propertyDao.insert(property)
    }

    suspend fun insertPhoto(photo: Photo): Long {
        return photoDao.insert(photo)
    }

    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest): Long {
        return pointOfInterestDao.insert(pointOfInterest)
    }
}

