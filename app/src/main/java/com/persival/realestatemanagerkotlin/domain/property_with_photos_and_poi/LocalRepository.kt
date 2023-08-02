package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import kotlinx.coroutines.flow.Flow

interface LocalRepository {

    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>>

    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity>

}
