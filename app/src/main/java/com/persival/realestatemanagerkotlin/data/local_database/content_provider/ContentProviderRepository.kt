package com.persival.realestatemanagerkotlin.data.local_database.content_provider

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import com.persival.realestatemanagerkotlin.data.local_database.model.PhotoDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PointOfInterestDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyWithPhotosAndPoisDto
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
            ContentProvider.PROPERTY_TABLE,
            null,
            null,
            null,
            null
        )
    }

    fun getAllProperties(): Flow<List<PropertyWithPhotosAndPoisDto>> {
        return flow {
            val cursor = queryAllProperties()
            val properties = mutableListOf<PropertyWithPhotosAndPoisDto>()

            cursor?.use {
                while (it.moveToNext()) {
                    convertCursorToPropertyWithPhotosAndPoisDto(it).let { it1 -> properties.add(it1) }
                }
            }

            emit(properties)
        }
    }

    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPoisDto?> {
        return flow {
            val uri = ContentUris.withAppendedId(ContentProvider.PROPERTY_TABLE, propertyId)
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val property: PropertyWithPhotosAndPoisDto? = cursor?.use {
                if (it.moveToFirst()) {
                    convertCursorToPropertyWithPhotosAndPoisDto(it)
                } else {
                    null
                }
            }

            emit(property)
        }
    }

    private fun getPhotosForProperty(propertyId: Long): List<PhotoDto> {
        val uri = ContentProvider.PHOTO_TABLE
        val selection = "propertyId = ?"
        val selectionArgs = arrayOf(propertyId.toString())

        val photos = mutableListOf<PhotoDto>()

        context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
            while (it.moveToNext()) {
                val photoDto = PhotoDto(
                    id = it.getLong(it.getColumnIndexOrThrow("id")),
                    propertyId = it.getLong(it.getColumnIndexOrThrow("propertyId")),
                    description = it.getString(it.getColumnIndexOrThrow("description")),
                    photoUrl = it.getString(it.getColumnIndexOrThrow("photoUrl"))
                )
                photos.add(photoDto)
            }
        }
        return photos
    }

    private fun getPOIsForProperty(propertyId: Long): List<PointOfInterestDto> {
        val uri = ContentProvider.POI_TABLE
        val selection = "propertyId = ?"
        val selectionArgs = arrayOf(propertyId.toString())

        val pois = mutableListOf<PointOfInterestDto>()

        context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
            while (it.moveToNext()) {
                val poiDto = PointOfInterestDto(
                    id = it.getLong(it.getColumnIndexOrThrow("id")),
                    propertyId = it.getLong(it.getColumnIndexOrThrow("propertyId")),
                    poi = it.getString(it.getColumnIndexOrThrow("poi"))
                )
                pois.add(poiDto)
            }
        }
        return pois
    }

    private fun convertCursorToPropertyWithPhotosAndPoisDto(cursor: Cursor): PropertyWithPhotosAndPoisDto {
        val propertyDto = PropertyDto(
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
        val photos = getPhotosForProperty(propertyDto.id)
        val poi = getPOIsForProperty(propertyDto.id)

        return PropertyWithPhotosAndPoisDto(propertyDto, photos, poi)
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
