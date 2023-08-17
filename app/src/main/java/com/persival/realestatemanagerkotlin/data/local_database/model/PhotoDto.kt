package com.persival.realestatemanagerkotlin.data.local_database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
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
)