package com.persival.realestatemanagerkotlin.domain.poi

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity

@Entity(
    tableName = "point_of_interest",
    foreignKeys = [ForeignKey(
        entity = PropertyEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("propertyId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PointOfInterestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long,
    val poi: String
)
