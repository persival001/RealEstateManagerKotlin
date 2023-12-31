package com.persival.realestatemanagerkotlin.data.local_database.point_of_interest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyEntity

@Entity(
    indices = [Index("propertyId")],
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
    @ColumnInfo(name = "propertyId") val propertyId: Long,
    val poi: String,
    val lastModified: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
