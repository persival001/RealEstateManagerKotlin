package com.persival.realestatemanagerkotlin.domain.photo.model

data class Photo(
    val id: Long = 0,
    val propertyId: Long,
    val description: String,
    val photoUrl: String,
)
