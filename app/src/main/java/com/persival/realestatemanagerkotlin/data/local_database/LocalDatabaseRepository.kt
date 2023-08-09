package com.persival.realestatemanagerkotlin.data.local_database

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import com.persival.realestatemanagerkotlin.data.ContentDataProvider
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity.Companion.toContentValues
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity.Companion.toContentValues
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity.Companion.toContentValues
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDatabaseRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val context: Context
) : LocalRepository {

    override suspend fun insertProperty(propertyEntity: PropertyEntity): Long {
        val contentValues = toContentValues(propertyEntity)
        val insertedUri = context.contentResolver.insert(ContentDataProvider.PROPERTY_TABLE, contentValues)
        return insertedUri?.lastPathSegment?.toLong() ?: 0L
    }

    override suspend fun insertPhoto(photoEntity: PhotoEntity): Long {
        val uri = ContentDataProvider.PHOTO_TABLE
        val contentValues = photoEntity.toContentValues()
        val resultUri = context.contentResolver.insert(uri, contentValues)
        return ContentUris.parseId(resultUri!!)
    }

    override suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long {
        val uri = ContentDataProvider.POI_TABLE
        val contentValues = pointOfInterestEntity.toContentValues()
        val resultUri = context.contentResolver.insert(uri, contentValues)
        return ContentUris.parseId(resultUri!!)
    }

    override suspend fun updateProperty(propertyEntity: PropertyEntity): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.PROPERTY_TABLE, propertyEntity.id!!)
        val contentValues = toContentValues(propertyEntity)
        return context.contentResolver.update(uri, contentValues, null, null)
    }

    override suspend fun updatePhoto(photoEntity: PhotoEntity): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.PHOTO_TABLE, photoEntity.id)
        val contentValues = photoEntity.toContentValues()
        return context.contentResolver.update(uri, contentValues, null, null)
    }

    override suspend fun updatePointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.POI_TABLE, pointOfInterestEntity.id)
        val contentValues = pointOfInterestEntity.toContentValues()
        return context.contentResolver.update(uri, contentValues, null, null)
    }

    override suspend fun deleteProperty(propertyId: Long): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.PROPERTY_TABLE, propertyId)
        return context.contentResolver.delete(uri, null, null)
    }

    override suspend fun deletePhoto(photoId: Long): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.PHOTO_TABLE, photoId)
        return context.contentResolver.delete(uri, null, null)
    }

    override suspend fun deletePointOfInterest(poiId: Long): Int {
        val uri = ContentUris.withAppendedId(ContentDataProvider.POI_TABLE, poiId)
        return context.contentResolver.delete(uri, null, null)
    }

    override suspend fun queryAllProperties(): Cursor? {
        return context.contentResolver.query(ContentDataProvider.PROPERTY_TABLE, null, null, null, null)
    }

    override fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>> {
        return flow {
            val cursor = context.contentResolver.query(ContentDataProvider.PROPERTY_TABLE, null, null, null, null)
            val properties = mutableListOf<PropertyWithPhotosAndPOIEntity>()

            cursor?.use {
                while (it.moveToNext()) {
                    convertCursorToPropertyWithPhotosAndPOIEntity(it)?.let { it1 -> properties.add(it1) }
                }
            }

            emit(properties)
        }
    }

    override fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity> {
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

    override suspend fun updatePropertyWithPhotosAndPOIs(
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

    private fun convertCursorToPropertyWithPhotosAndPOIEntity(cursor: Cursor): PropertyWithPhotosAndPOIEntity? {
        val propertyIdIndex = cursor.getColumnIndex("property_id")
        val propertyNameIndex = cursor.getColumnIndex("property_name")

        if (propertyIdIndex == -1 || propertyNameIndex == -1) {
            return null
        }

        val propertyId = cursor.getLong(propertyIdIndex)
        val propertyName = cursor.getString(propertyNameIndex)

        val propertyEntity = PropertyEntity(
            propertyId,
            propertyName,
            "",
            120,
            4,
            3,
            2,
            "re",
            250000,
            false,
            "12/08/2023",
            "",
            "JP"
        )
        val photos = listOf<PhotoEntity>() // Remplir cette liste depuis le curseur si nécessaire
        val pois = listOf<PointOfInterestEntity>() // Remplir cette liste depuis le curseur si nécessaire

        return PropertyWithPhotosAndPOIEntity(propertyEntity, photos, pois)
    }


}


