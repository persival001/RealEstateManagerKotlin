package com.persival.realestatemanagerkotlin.domain.point_of_interest

import android.content.ContentValues
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
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val propertyId: Long,
    var poi: String
) {
    companion object {
        fun fromContentValues(values: ContentValues?): PointOfInterestEntity {
            return PointOfInterestEntity(
                id = values?.getAsLong("id") ?: 0L,
                propertyId = values?.getAsLong("propertyId") ?: 0L,
                poi = values?.getAsString("poi") ?: ""
            )
        }

        fun PointOfInterestEntity.toContentValues(): ContentValues {
            return ContentValues().apply {
                put("id", id)
                put("propertyId", propertyId)
                put("poi", poi)
            }
        }

    }
}
