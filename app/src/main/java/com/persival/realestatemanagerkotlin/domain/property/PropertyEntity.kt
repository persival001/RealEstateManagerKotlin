package com.persival.realestatemanagerkotlin.domain.property

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "property")
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val address: String,
    val latLng: String,
    val area: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    val price: BigDecimal,
    val isSold: Boolean,
    val entryDate: String,
    val saleDate: String?,
    val agentName: String
)