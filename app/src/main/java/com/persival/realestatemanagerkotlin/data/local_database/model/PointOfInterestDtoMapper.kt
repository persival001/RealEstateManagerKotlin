package com.persival.realestatemanagerkotlin.data.local_database.model

import com.persival.realestatemanagerkotlin.data.local_database.util.DomainMapper
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity


class PointOfInterestDtoMapper : DomainMapper<PointOfInterestDto, PointOfInterestEntity> {

    override fun mapToEntity(model: PointOfInterestDto): PointOfInterestEntity {
        return PointOfInterestEntity(
            id = model.id,
            propertyId = model.propertyId,
            poi = model.poi,
        )
    }

    override fun mapFromDomainModel(domainModel: PointOfInterestEntity): PointOfInterestDto {
        return PointOfInterestDto(
            id = domainModel.id ?: 0L,
            propertyId = domainModel.propertyId,
            poi = domainModel.poi,
        )
    }

    fun fromEntityList(initial: List<PointOfInterestDto>): List<PointOfInterestEntity> {
        return initial.map { mapToEntity(it) }
    }

    fun toEntityList(initial: List<PointOfInterestEntity>): List<PointOfInterestDto> {
        return initial.map { mapFromDomainModel(it) }
    }

}
