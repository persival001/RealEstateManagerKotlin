package com.persival.realestatemanagerkotlin.data.local_database.model

import androidx.room.Embedded
import androidx.room.Relation

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

