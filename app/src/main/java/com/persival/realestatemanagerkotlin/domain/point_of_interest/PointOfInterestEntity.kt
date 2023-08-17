package com.persival.realestatemanagerkotlin.domain.point_of_interest

data class PointOfInterestEntity(
    val id: Long? = null,
    val propertyId: Long,
    val poi: String,
)
