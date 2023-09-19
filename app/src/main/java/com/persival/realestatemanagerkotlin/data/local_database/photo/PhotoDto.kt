package com.persival.realestatemanagerkotlin.data.local_database.photo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDto

@Entity(
    indices = [Index("propertyId")],
    tableName = "photo",
    foreignKeys = [
        ForeignKey(
            entity = PropertyDto::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("propertyId"),
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class PhotoDto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "propertyId") val propertyId: Long,
    val description: String,
    @ColumnInfo(name = "photo_url") val photoUrl: String,
    val lastModified: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)