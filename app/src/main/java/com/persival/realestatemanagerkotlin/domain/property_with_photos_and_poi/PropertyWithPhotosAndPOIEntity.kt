package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity

data class PropertyWithPhotosAndPOIEntity(
    val property: PropertyEntity,
    val photos: List<PhotoEntity>,
    val pointsOfInterest: List<PointOfInterestEntity>,
)

