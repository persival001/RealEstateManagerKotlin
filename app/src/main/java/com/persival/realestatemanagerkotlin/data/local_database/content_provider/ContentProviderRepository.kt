package com.persival.realestatemanagerkotlin.data.local_database.content_provider

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentProviderRepository @Inject constructor(
    private val context: Context,
) {

    private fun queryAllProperties(): Cursor? {
        return context.contentResolver.query(
            ContentDataProvider.PROPERTY_TABLE,
            null,
            null,
            null,
            null
        )
    }

    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>> {
        return flow {
            val cursor = queryAllProperties()
            val properties = mutableListOf<PropertyWithPhotosAndPOIEntity>()

            cursor?.use {
                while (it.moveToNext()) {
                    convertCursorToPropertyWithPhotosAndPOIEntity(it).let { it1 -> properties.add(it1) }
                }
            }

            emit(properties)
        }
    }

    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity?> {
        return flow {
            val uri = ContentUris.withAppendedId(ContentDataProvider.PROPERTY_TABLE, propertyId)
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val property: PropertyWithPhotosAndPOIEntity? = cursor?.use {
                if (it.moveToFirst()) {
                    convertCursorToPropertyWithPhotosAndPOIEntity(it)
                } else {
                    null
                }
            }

            emit(property)
        }
    }

    private fun getPhotosForProperty(propertyId: Long): List<PhotoEntity> {
        val uri = ContentDataProvider.PHOTO_TABLE
        val selection = "propertyId = ?"
        val selectionArgs = arrayOf(propertyId.toString())

        val photos = mutableListOf<PhotoEntity>()

        context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
            while (it.moveToNext()) {
                val photoEntity = PhotoEntity(
                    id = it.getLong(it.getColumnIndexOrThrow("id")),
                    propertyId = it.getLong(it.getColumnIndexOrThrow("propertyId")),
                    description = it.getString(it.getColumnIndexOrThrow("description")),
                    photoUrl = it.getString(it.getColumnIndexOrThrow("photoUrl"))
                )
                photos.add(photoEntity)
            }
        }
        return photos
    }

    private fun getPOIsForProperty(propertyId: Long): List<PointOfInterestEntity> {
        val uri = ContentDataProvider.POI_TABLE
        val selection = "propertyId = ?"
        val selectionArgs = arrayOf(propertyId.toString())

        val pois = mutableListOf<PointOfInterestEntity>()

        context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
            while (it.moveToNext()) {
                val poiEntity = PointOfInterestEntity(
                    id = it.getLong(it.getColumnIndexOrThrow("id")),
                    propertyId = it.getLong(it.getColumnIndexOrThrow("propertyId")),
                    poi = it.getString(it.getColumnIndexOrThrow("poi"))
                )
                pois.add(poiEntity)
            }
        }
        return pois
    }

    private fun convertCursorToPropertyWithPhotosAndPOIEntity(cursor: Cursor): PropertyWithPhotosAndPOIEntity {
        val propertyEntity = PropertyEntity(
            id = cursor.getLong("id"),
            type = cursor.getString("type"),
            address = cursor.getString("address"),
            latLng = cursor.getString("latLng"),
            area = cursor.getInt("area"),
            rooms = cursor.getInt("rooms"),
            bathrooms = cursor.getInt("bathrooms"),
            bedrooms = cursor.getInt("bedrooms"),
            description = cursor.getString("description"),
            price = cursor.getInt("price"),
            isSold = cursor.getBoolean("isSold"),
            entryDate = cursor.getString("entryDate"),
            saleDate = cursor.getString("saleDate"),
            agentName = cursor.getString("agentName")
        )

        // Fetch associated photos and POIs for the property
        val photos = getPhotosForProperty(propertyEntity.id)
        val poi = getPOIsForProperty(propertyEntity.id)

        return PropertyWithPhotosAndPOIEntity(propertyEntity, photos, poi)
    }

    private fun Cursor.getString(columnName: String): String {
        return getString(getColumnIndexOrThrow(columnName))
    }

    private fun Cursor.getInt(columnName: String): Int {
        return getInt(getColumnIndexOrThrow(columnName))
    }

    private fun Cursor.getLong(columnName: String): Long {
        return getLong(getColumnIndexOrThrow(columnName))
    }

    private fun Cursor.getBoolean(columnName: String): Boolean {
        return getInt(columnName) != 0
    }

}
