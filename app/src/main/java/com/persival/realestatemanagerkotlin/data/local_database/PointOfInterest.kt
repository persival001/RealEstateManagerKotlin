package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "point_of_interest",
    foreignKeys = [ForeignKey(
        entity = Property::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("propertyId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PointOfInterest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long,
    val school: Boolean,
    val park: Boolean,
    val store: Boolean,
    val hospital: Boolean,
    val bus: Boolean
)
