package com.persival.realestatemanagerkotlin.ui.modify_property

data class ModifyPropertyViewState(
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
    val pointsOfInterest: String,
)
