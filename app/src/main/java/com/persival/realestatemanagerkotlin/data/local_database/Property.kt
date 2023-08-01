package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class Property(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val address: String,
    val area: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    val price: Int,
    val status: Boolean,
    val entryDate: String,
    val saleDate: String?,
    val agentId: Long
)
