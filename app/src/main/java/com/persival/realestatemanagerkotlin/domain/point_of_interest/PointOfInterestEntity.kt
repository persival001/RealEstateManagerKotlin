package com.persival.realestatemanagerkotlin.domain.point_of_interest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity

@Entity(
    tableName = "point_of_interest",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("propertyId"),
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class PointOfInterestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    val poi: String
)
