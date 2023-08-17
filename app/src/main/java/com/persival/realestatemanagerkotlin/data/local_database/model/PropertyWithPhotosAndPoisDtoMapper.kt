package com.persival.realestatemanagerkotlin.data.local_database.model

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import javax.inject.Inject

class PropertyWithPhotosAndPoisDtoMapper @Inject constructor(
    private val propertyMapper: PropertyDtoMapper,
    private val photoMapper: PhotoDtoMapper,
    private val poiMapper: PointOfInterestDtoMapper
) {

    fun mapToEntity(model: PropertyWithPhotosAndPoisDto): PropertyWithPhotosAndPOIEntity {
        return PropertyWithPhotosAndPOIEntity(
            property = propertyMapper.mapToEntity(model.property),
            photos = model.photos.map { photoMapper.mapToEntity(it) },
            pointsOfInterest = model.pointsOfInterest.map { poiMapper.mapToEntity(it) }
        )
    }

    fun mapFromDomainModel(domainModel: PropertyWithPhotosAndPOIEntity): PropertyWithPhotosAndPoisDto {
        return PropertyWithPhotosAndPoisDto(
            property = propertyMapper.mapFromDomainModel(domainModel.property),
            photos = domainModel.photos.map { photoMapper.mapFromDomainModel(it) },
            pointsOfInterest = domainModel.pointsOfInterest.map { poiMapper.mapFromDomainModel(it) }
        )
    }
}

