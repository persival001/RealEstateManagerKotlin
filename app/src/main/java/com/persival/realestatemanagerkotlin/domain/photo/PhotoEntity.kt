package com.persival.realestatemanagerkotlin.domain.photo

data class PhotoEntity(
    val id: Long = 0,
    val propertyId: Long,
    val description: String,
    val photoUrl: String,
)
