package com.persival.realestatemanagerkotlin.domain.point_of_interest.model

data class PointOfInterest(
    val id: Long = 0L,
    val propertyId: Long,
    val poi: String,
)
