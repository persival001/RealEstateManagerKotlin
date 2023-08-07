package com.persival.realestatemanagerkotlin.domain.photo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity

@Entity(
    tableName = "photo",
    foreignKeys = [ForeignKey(
        entity = PropertyEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("propertyId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long,
    var description: String,
    var photoUrl: String
)

