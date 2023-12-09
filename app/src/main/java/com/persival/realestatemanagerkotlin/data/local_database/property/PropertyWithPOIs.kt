package com.persival.realestatemanagerkotlin.data.local_database.property

import androidx.room.Embedded
import androidx.room.Relation
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestEntity

data class PropertyWithPOIs(
    @Embedded val property: PropertyEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "propertyId"
    )
    val pois: List<PointOfInterestEntity>
)
