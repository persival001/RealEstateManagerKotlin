package com.persival.realestatemanagerkotlin.data.local_database.model

import com.persival.realestatemanagerkotlin.data.local_database.util.DomainMapper
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity

class PropertyWithPhotosAndPoisDtoMapper(
    private val propertyMapper: PropertyDtoMapper,
    private val photoMapper: PhotoDtoMapper,
    private val poiMapper: PointOfInterestDtoMapper
) : DomainMapper<PropertyWithPhotosAndPoisDto, PropertyWithPhotosAndPOIEntity> {

    override fun mapToDomainModel(model: PropertyWithPhotosAndPoisDto): PropertyWithPhotosAndPOIEntity {
        return PropertyWithPhotosAndPOIEntity(
            property = propertyMapper.mapToDomainModel(model.property),
            photos = model.photos.map { photoMapper.mapToDomainModel(it) },
            pointsOfInterest = model.pointsOfInterest.map { poiMapper.mapToDomainModel(it) }
        )
    }

    override fun mapFromDomainModel(domainModel: PropertyWithPhotosAndPOIEntity): PropertyWithPhotosAndPoisDto {
        return PropertyWithPhotosAndPoisDto(
            property = propertyMapper.mapFromDomainModel(domainModel.property),
            photos = domainModel.photos.map { photoMapper.mapFromDomainModel(it) },
            pointsOfInterest = domainModel.pointsOfInterest.map { poiMapper.mapFromDomainModel(it) }
        )
    }

    fun fromEntityList(initial: List<PropertyWithPhotosAndPoisDto>): List<PropertyWithPhotosAndPOIEntity> {
        return initial.map { mapToDomainModel(it) }
    }

    fun toEntityList(initial: List<PropertyWithPhotosAndPOIEntity>): List<PropertyWithPhotosAndPoisDto> {
        return initial.map { mapFromDomainModel(it) }
    }
}

