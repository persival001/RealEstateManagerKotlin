package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class Property(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val price: Double,
    val area: Double,
    val rooms: Int,
    val description: String,
    val address: String,
    val status: String,
    val entryDate: Long,
    val saleDate: Long?,
    val agentId: Long
)
