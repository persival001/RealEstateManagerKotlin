package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model.PropertyWithPhotosAndPOI
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPropertyWithPhotoAndPOIUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    fun invoke(propertyId: Long): Flow<PropertyWithPhotosAndPOI> = localRepository.getPropertyById(propertyId)
}
