package com.persival.realestatemanagerkotlin.domain.photo

import android.content.ContentValues
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
) {
    companion object {
        fun fromContentValues(values: ContentValues?): PhotoEntity {
            return PhotoEntity(
                id = values?.getAsLong("id") ?: 0L,
                propertyId = values?.getAsLong("propertyId") ?: 0L,
                description = values?.getAsString("description") ?: "",
                photoUrl = values?.getAsString("photoUrl") ?: ""
            )
        }

        fun PhotoEntity.toContentValues(): ContentValues {
            return ContentValues().apply {
                put("id", id)
                put("propertyId", propertyId)
                put("description", description)
                put("photoUrl", photoUrl)
            }
        }
    }

}


