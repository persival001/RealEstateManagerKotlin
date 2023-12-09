package com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois

import androidx.room.Embedded
import androidx.room.Relation
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyEntity

data class PropertyWithPhotosAndPoisEntity(
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
    val pointsOfInterest: List<PointOfInterestEntity>,
)

