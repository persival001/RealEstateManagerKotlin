package com.persival.realestatemanagerkotlin.data.local_database

import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDatabaseRepository @Inject constructor(
    private val propertyDao: PropertyDao
) {

    suspend fun insertProperty(property: Property) {
        propertyDao.insert(property)
    }
}
