package com.persival.realestatemanagerkotlin.data.local_database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class PropertyDto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val address: String,
    val latLng: String,
    val area: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    val price: Int,
    val isSold: Boolean,
    val entryDate: String,
    val saleDate: String?,
    val agentName: String,
    val lastModified: Long,
    val isSynced: Boolean,
)