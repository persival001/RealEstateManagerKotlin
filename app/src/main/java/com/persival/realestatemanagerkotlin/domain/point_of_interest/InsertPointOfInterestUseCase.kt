package com.persival.realestatemanagerkotlin.domain.point_of_interest

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertPointOfInterestUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend fun invoke(pointOfInterestEntity: PointOfInterestEntity): Long {
        return localRepository.insertPointOfInterest(pointOfInterestEntity)
    }
}