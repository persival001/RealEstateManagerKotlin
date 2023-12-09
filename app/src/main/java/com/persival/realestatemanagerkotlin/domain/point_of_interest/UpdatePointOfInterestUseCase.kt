package com.persival.realestatemanagerkotlin.domain.point_of_interest

import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePointOfInterestUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend fun invoke(propertyId: Long, pointOfInterestEntities: List<PointOfInterest>) {
        localRepository.updatePointOfInterestWithPropertyId(propertyId, pointOfInterestEntities)
    }
}