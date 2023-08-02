package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun getPropertyWithPhotosAndPOI(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity>
}
