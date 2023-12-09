package com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois

import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoEntityMapper
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestEntityMapper
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyEntityMapper
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model.PropertyWithPhotosAndPOI
import javax.inject.Inject

class PropertyWithPhotosAndPoisEntityMapper @Inject constructor(
    private val propertyMapper: PropertyEntityMapper,
    private val photoMapper: PhotoEntityMapper,
    private val poiMapper: PointOfInterestEntityMapper
) {

    fun mapToEntity(model: PropertyWithPhotosAndPoisEntity): PropertyWithPhotosAndPOI {
        return PropertyWithPhotosAndPOI(
            property = propertyMapper.mapPropertyDtoToPropertyEntity(model.property),
            photos = model.photos.map { photoMapper.mapPhotoDtoToPhotoEntity(it) },
            pointsOfInterest = model.pointsOfInterest.map { poiMapper.mapPointOfInterestDtoToPointOfInterestEntity(it) }
        )
    }

    fun mapFromDomainModel(domainModel: PropertyWithPhotosAndPOI): PropertyWithPhotosAndPoisEntity {
        return PropertyWithPhotosAndPoisEntity(
            property = propertyMapper.mapPropertyEntityToPropertyDto(domainModel.property),
            photos = domainModel.photos.map { photoMapper.mapPhotoEntityToPhotoDto(it) },
            pointsOfInterest = domainModel.pointsOfInterest.map {
                poiMapper.mapPointOfInterestEntityToPointOfInterestDto(
                    it
                )
            }
        )
    }

}

