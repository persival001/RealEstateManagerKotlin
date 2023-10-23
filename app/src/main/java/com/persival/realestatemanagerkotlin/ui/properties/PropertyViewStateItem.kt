package com.persival.realestatemanagerkotlin.ui.properties

data class PropertyViewStateItem(
    val id: Long,
    val type: String,
    val address: String,
    val latLng: String?,
    val price: String,
    val rooms: String,
    val surface: String,
    val bathrooms: String,
    val bedrooms: String,
    val poi: String,
    val pictureUri: String,
    val isSold: Boolean
)