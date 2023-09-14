package com.persival.realestatemanagerkotlin.domain.point_of_interest

data class PointOfInterestEntity(
    val id: Long = 0L,
    val propertyId: Long,
    val poi: String,
)
