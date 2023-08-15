package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePropertyWithPhotoAndPOIUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend fun invoke(
        property: PropertyEntity,
        photos: List<PhotoEntity>,
        pois: List<PointOfInterestEntity>
    ) {
        localRepository.updatePropertyWithPhotosAndPOIs(property, photos, pois)
    }
}