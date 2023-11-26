package com.persival.realestatemanagerkotlin.domain.search

data class SearchEntity(
    val type: String? = "",
    val minPrice: Int?,
    val maxPrice: Int?,
    val minArea: Int?,
    val maxArea: Int?,
    val isSold: Boolean?,
    val timeFilter: String?,
    val poi: List<String>?
)