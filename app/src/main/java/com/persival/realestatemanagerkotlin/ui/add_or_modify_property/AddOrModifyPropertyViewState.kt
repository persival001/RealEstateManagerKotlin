package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

data class AddOrModifyPropertyViewState(
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
