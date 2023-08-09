package com.persival.realestatemanagerkotlin.domain.property

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var type: String,
    var address: String,
    val area: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    val price: Int,
    val isSold: Boolean,
    val entryDate: String,
    val saleDate: String?,
    val agentName: String
) {
    companion object {
        fun fromContentValues(values: ContentValues?): PropertyEntity {
            return PropertyEntity(
                id = values?.getAsLong("id"),
                type = values?.getAsString("type") ?: "",
                address = values?.getAsString("address") ?: "",
                area = values?.getAsInteger("area") ?: 0,
                rooms = values?.getAsInteger("rooms") ?: 0,
                bathrooms = values?.getAsInteger("bathrooms") ?: 0,
                bedrooms = values?.getAsInteger("bedrooms") ?: 0,
                description = values?.getAsString("description") ?: "",
                price = values?.getAsInteger("price") ?: 0,
                isSold = values?.getAsBoolean("isSold") ?: false,
                entryDate = values?.getAsString("entryDate") ?: "",
                saleDate = values?.getAsString("saleDate"),
                agentName = values?.getAsString("agentName") ?: ""
            )
        }

        fun toContentValues(propertyEntity: PropertyEntity): ContentValues {
            val contentValues = ContentValues()
            propertyEntity.id?.let { contentValues.put("id", it) }
            contentValues.put("type", propertyEntity.type)
            contentValues.put("address", propertyEntity.address)
            contentValues.put("area", propertyEntity.area)
            contentValues.put("rooms", propertyEntity.rooms)
            contentValues.put("bathrooms", propertyEntity.bathrooms)
            contentValues.put("bedrooms", propertyEntity.bedrooms)
            contentValues.put("description", propertyEntity.description)
            contentValues.put("price", propertyEntity.price)
            contentValues.put("isSold", propertyEntity.isSold)
            contentValues.put("entryDate", propertyEntity.entryDate)
            contentValues.put("saleDate", propertyEntity.saleDate)
            contentValues.put("agentName", propertyEntity.agentName)
            return contentValues
        }

    }
}


