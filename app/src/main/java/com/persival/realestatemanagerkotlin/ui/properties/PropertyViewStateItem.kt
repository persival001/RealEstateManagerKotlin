package com.persival.realestatemanagerkotlin.ui.properties

data class PropertyViewStateItem(
    val id: Long,
    val type: String,
    val address: String,
    val price: String,
    val pictureUri: String,
    val isSold: Boolean
)