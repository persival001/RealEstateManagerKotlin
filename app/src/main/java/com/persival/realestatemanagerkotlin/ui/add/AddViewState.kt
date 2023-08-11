package com.persival.realestatemanagerkotlin.ui.add

data class AddViewState(
    val type: String,
    val address: String,
    val latLng: String,
    val area: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    val price: Int,
    val availableFrom: String,
    val soldAt: String,
    val photoUris: List<String>,
    val photoDescriptions: List<String>,
    val pointsOfInterest: String,
)
