package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.poi.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity

data class PropertyWithPhotosAndPOIEntity(
    val property: PropertyEntity,
    val photos: List<PhotoEntity>,
    val poi: List<PointOfInterestEntity>
)
