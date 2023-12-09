package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model

import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import com.persival.realestatemanagerkotlin.domain.property.model.Property

data class PropertyWithPhotosAndPOI(
    val property: Property,
    val photos: List<Photo>,
    val pointsOfInterest: List<PointOfInterest>,
)

