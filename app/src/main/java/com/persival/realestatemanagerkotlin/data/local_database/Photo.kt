package com.persival.realestatemanagerkotlin.data.local_database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "photo",
    foreignKeys = [ForeignKey(entity = Property::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("propertyId"),
        onDelete = ForeignKey.CASCADE)])
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long,
    val description: String,
    val url: String
)
