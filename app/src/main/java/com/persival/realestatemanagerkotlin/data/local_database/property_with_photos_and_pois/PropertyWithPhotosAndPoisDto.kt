package com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois

import androidx.room.Embedded
import androidx.room.Relation
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDto
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDto
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDto

data class PropertyWithPhotosAndPoisDto(
    @Embedded val property: PropertyDto,
    @Relation(
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val photos: List<PhotoDto>,
    @Relation(
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val pointsOfInterest: List<PointOfInterestDto>,
)

