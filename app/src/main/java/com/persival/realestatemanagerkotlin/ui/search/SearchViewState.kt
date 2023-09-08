package com.persival.realestatemanagerkotlin.ui.search

data class SearchViewState(
    val type: String,
    val minPrice: Int,
    val maxPrice: Int,
    val minArea: Int,
    val maxArea: Int,
    val pois: String,
    val date: String
)