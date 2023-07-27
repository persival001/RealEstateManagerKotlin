package com.persival.realestatemanagerkotlin.ui.add

import java.sql.Timestamp
import java.util.TimeZone

data class AddViewState(
    val propertyType: String,
    val propertyPrice: Long,
    val propertyArea: String,
    val propertyRooms: String,
    val propertyDescription: String,
    val propertyAddress: String,
    val propertyStatus: String,
    val propertyEntryDate: Timestamp,
    val propertySaleDate: Timestamp,
    val propertyAgent: String,
    val propertyPhotos: List<String>,
    val propertyPointOfInterest: List<String>
)
