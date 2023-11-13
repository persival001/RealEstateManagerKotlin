package com.persival.realestatemanagerkotlin.data.local_database.property

import androidx.room.Embedded
import androidx.room.Relation
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDto

data class PropertyWithPOIs(
    @Embedded val property: PropertyDto,
    @Relation(
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val pois: List<PointOfInterestDto>
)
