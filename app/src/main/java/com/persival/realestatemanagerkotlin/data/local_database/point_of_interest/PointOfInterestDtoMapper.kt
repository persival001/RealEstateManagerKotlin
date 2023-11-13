package com.persival.realestatemanagerkotlin.data.local_database.point_of_interest

import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import javax.inject.Inject


class PointOfInterestDtoMapper @Inject constructor() {

    fun mapToEntity(model: PointOfInterestDto): PointOfInterestEntity {
        return PointOfInterestEntity(
            id = model.id,
            propertyId = model.propertyId,
            poi = model.poi,
        )
    }

    fun mapFromDomainModel(domainModel: PointOfInterestEntity): PointOfInterestDto {
        return PointOfInterestDto(
            id = domainModel.id,
            propertyId = domainModel.propertyId,
            poi = domainModel.poi,
        )
    }

}
