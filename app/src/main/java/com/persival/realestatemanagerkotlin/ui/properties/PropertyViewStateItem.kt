package com.persival.realestatemanagerkotlin.ui.properties

data class PropertyViewStateItem (
    val id: Int,
    val type: String,
    val address: String,
    val price: String,
    val picture: Int,
    val isSold: Boolean
)