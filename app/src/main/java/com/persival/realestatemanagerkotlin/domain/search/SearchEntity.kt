package com.persival.realestatemanagerkotlin.domain.search

data class SearchEntity(
    val type: String?,
    val minPrice: Int?,
    val maxPrice: Int?,
    val minArea: Int?,
    val maxArea: Int?,
    val minRooms: Int?,
    val maxRooms: Int?,
    val isSold: Boolean?,
    val latLng: String?,
    val entryDate: String?,
    val poi: String?
)