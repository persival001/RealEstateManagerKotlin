package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import androidx.room.Embedded
import androidx.room.Relation
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.poi.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity

data class PropertyWithPhotosAndPOIEntity(
    @Embedded val property: PropertyEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val photos: List<PhotoEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val pointsOfInterest: List<PointOfInterestEntity>
)

