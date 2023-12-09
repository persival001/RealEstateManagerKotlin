package com.persival.realestatemanagerkotlin.data.local_database.point_of_interest

import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import javax.inject.Inject


class PointOfInterestEntityMapper @Inject constructor() {

    fun mapPointOfInterestDtoToPointOfInterestEntity(pointOfInterestEntity: PointOfInterestEntity): PointOfInterest {
        return PointOfInterest(
            id = pointOfInterestEntity.id,
            propertyId = pointOfInterestEntity.propertyId,
            poi = pointOfInterestEntity.poi,
        )
    }

    fun mapPointOfInterestEntityToPointOfInterestDto(pointOfInterest: PointOfInterest): PointOfInterestEntity {
        return PointOfInterestEntity(
            id = pointOfInterest.id,
            propertyId = pointOfInterest.propertyId,
            poi = pointOfInterest.poi,
        )
    }

}
