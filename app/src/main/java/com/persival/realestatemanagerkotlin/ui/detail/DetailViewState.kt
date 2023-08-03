package com.persival.realestatemanagerkotlin.ui.detail

data class DetailViewState(
    val propertyId: Long,
    val type: String,
    val price: String,
    val surface: String,
    val rooms: String,
    val bedrooms: String,
    val bathrooms: String,
    val description: String,
    val address: String,
    val pointOfInterest: String,
    val isSold: Boolean,
    val entryDate: String,
    val saleDate: String,
    val agentName: String
)
