package com.persival.realestatemanagerkotlin.domain.property.model

data class Property(
    val id: Long,
    val type: String,
    val address: String,
    val latLng: String,
    val area: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    val price: Int,
    val isSold: Boolean,
    val entryDate: String,
    val saleDate: String?,
    val agentName: String,
)