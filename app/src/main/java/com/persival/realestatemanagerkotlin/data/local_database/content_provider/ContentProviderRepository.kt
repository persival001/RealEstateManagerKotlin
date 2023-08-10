package com.persival.realestatemanagerkotlin.data.local_database.content_provider

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity.Companion.toContentValues
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity.Companion.toContentValues
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity.Companion.toContentValues
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentProviderRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val context: Context
) {

    suspend fun insertProperty(propertyEntity: PropertyEntity): Long {
        val contentValues = toContentValues(propertyEntity)
        val insertedUri = context.contentResolver.insert(ContentDataProvider.PROPERTY_TABLE, contentValues)
        return insertedUri?.lastPathSegment?.toLong() ?: 0L
    }

    suspend fun insertPhoto(photoEntity: PhotoEntity): Long {
        val uri = ContentDataProvider.PHOTO_TABLE
        val contentValues = photoEntity.toContentValues()
        val resultUri = context.contentResolver.insert(uri, contentValues)
        return ContentUris.parseId(resultUri!!)
    }

    suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long {
        val uri = ContentDataProvider.POI_TABLE
        val contentValues = pointOfInterestEntity.toContentValues()
        val resultUri = context.contentResolver.insert(uri, contentValues)
        return ContentUris.parseId(resultUri!!)
    }

    suspend fun updateProperty(propertyEntity: PropertyEntity): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.PROPERTY_TABLE, propertyEntity.id!!)
        val contentValues = toContentValues(propertyEntity)
        return context.contentResolver.update(uri, contentValues, null, null)
    }

    suspend fun updatePhoto(photoEntity: PhotoEntity): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.PHOTO_TABLE, photoEntity.id)
        val contentValues = photoEntity.toContentValues()
        return context.contentResolver.update(uri, contentValues, null, null)
    }

    suspend fun updatePointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.POI_TABLE, pointOfInterestEntity.id)
        val contentValues = pointOfInterestEntity.toContentValues()
        return context.contentResolver.update(uri, contentValues, null, null)
    }

    suspend fun deleteProperty(propertyId: Long): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.PROPERTY_TABLE, propertyId)
        return context.contentResolver.delete(uri, null, null)
    }

    suspend fun deletePhoto(photoId: Long): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.PHOTO_TABLE, photoId)
        return context.contentResolver.delete(uri, null, null)
    }

    suspend fun deletePointOfInterest(poiId: Long): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.POI_TABLE, poiId)
        return context.contentResolver.delete(uri, null, null)
    }

    suspend fun queryAllProperties(): Cursor? {
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
            val cursor = context.contentResolver.query(
                ContentDataProvider.PROPERTY_TABLE,
                null,
                null,
                null,
                null
            )
            val properties = mutableListOf<PropertyWithPhotosAndPOIEntity>()

            cursor?.use {
                while (it.moveToNext()) {
                    convertCursorToPropertyWithPhotosAndPOIEntity(it)?.let { it1 -> properties.add(it1) }
                }
            }

            emit(properties)
        }
    }

    fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity> {
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

            if (property != null) {
                emit(property)
            }
        }
    }

    suspend fun updatePropertyWithPhotosAndPOIs(
        property: PropertyEntity,
        photos: List<PhotoEntity>,
        pois: List<PointOfInterestEntity>
    ) {
        updateProperty(property)

        photos.forEach { photo ->
            updatePhoto(photo)
        }

        pois.forEach { poi ->
            updatePointOfInterest(poi)
        }
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
        val photos = listOf(
            PhotoEntity(
                id = cursor.getLong("id"),
                propertyId = cursor.getLong("propertyId"),
                description = cursor.getString("description"),
                photoUrl = cursor.getString("photoUrl")
            )
        )
        val pois = listOf(
            PointOfInterestEntity(
                id = cursor.getLong("id"),
                propertyId = cursor.getLong("propertyId"),
                poi = cursor.getString("poi")
            )
        )

        return PropertyWithPhotosAndPOIEntity(propertyEntity, photos, pois)
    }

    fun Cursor.getString(columnName: String): String {
        return getString(getColumnIndexOrThrow(columnName))
    }

    fun Cursor.getInt(columnName: String): Int {
        return getInt(getColumnIndexOrThrow(columnName))
    }

    fun Cursor.getLong(columnName: String): Long {
        return getLong(getColumnIndexOrThrow(columnName))
    }

    fun Cursor.getBoolean(columnName: String): Boolean {
        return getInt(columnName) != 0
    }


}


