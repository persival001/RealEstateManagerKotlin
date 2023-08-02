package com.persival.realestatemanagerkotlin.domain.property

import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertPropertyUseCase @Inject constructor(
    private val propertyDao: PropertyDao
) {
    suspend fun invoke(propertyEntity: PropertyEntity) {
        propertyDao.insert(propertyEntity)
    }
}