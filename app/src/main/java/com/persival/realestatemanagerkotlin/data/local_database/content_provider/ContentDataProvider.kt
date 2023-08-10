package com.persival.realestatemanagerkotlin.data.local_database.content_provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.persival.realestatemanagerkotlin.data.local_database.AppDatabase
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import kotlinx.coroutines.runBlocking

class ContentDataProvider : ContentProvider() {

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private lateinit var appDatabase: AppDatabase
    private lateinit var photoDao: PhotoDao
    private lateinit var pointOfInterestDao: PointOfInterestDao

    init {
        uriMatcher.addURI(AUTHORITY, "properties", PROPERTY)
        uriMatcher.addURI(AUTHORITY, "photos", PHOTO)
        uriMatcher.addURI(AUTHORITY, "points_of_interest", POI)
        uriMatcher.addURI(AUTHORITY, "properties/#", PROPERTY_ID)
    }

    companion object {
        const val AUTHORITY = "com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentDataProvider"
        val PROPERTY_TABLE: Uri = Uri.parse("content://$AUTHORITY/properties")
        val PHOTO_TABLE: Uri = Uri.parse("content://$AUTHORITY/photos")
        val POI_TABLE: Uri = Uri.parse("content://$AUTHORITY/points_of_interest")

        private const val PROPERTY = 1
        private const val PHOTO = 2
        private const val POI = 3
        private const val PROPERTY_ID = 4
    }

    private var propertyDao: PropertyDao? = null

    override fun onCreate(): Boolean {
        context?.let {
            appDatabase = Room.databaseBuilder(it, AppDatabase::class.java, "app_database").build()
            propertyDao = appDatabase.propertyDao()
            photoDao = appDatabase.photoDao()
            pointOfInterestDao = appDatabase.pointOfInterestDao()
        }
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor {
        val cursor: Cursor = when (uriMatcher.match(uri)) {
            PROPERTY -> {
                appDatabase.propertyDao().getAllPropertiesAsCursor()
            }

            PHOTO -> {
                appDatabase.photoDao().getAllPhotosAsCursor()
            }

            POI -> {
                appDatabase.pointOfInterestDao().getAllPointsOfInterestAsCursor()
            }

            PROPERTY_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull()
                    ?: throw IllegalArgumentException("ID must be provided for the property")
                appDatabase.propertyDao().getPropertyByIdAsCursor(id)
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            PROPERTY -> "vnd.android.cursor.dir/$AUTHORITY.properties"
            PHOTO -> "vnd.android.cursor.dir/$AUTHORITY.photos"
            POI -> "vnd.android.cursor.dir/$AUTHORITY.points_of_interest"
            PROPERTY_ID -> "vnd.android.cursor.item/$AUTHORITY.properties"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val context = context ?: return null
        val id: Long = when (uriMatcher.match(uri)) {
            PROPERTY -> propertyDao?.insert(PropertyEntity.fromContentValues(values)) ?: 0
            PHOTO -> photoDao.insert(PhotoEntity.fromContentValues(values))
            POI -> pointOfInterestDao.insert(PointOfInterestEntity.fromContentValues(values))
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context.contentResolver.notifyChange(uri, null)
        return Uri.parse("$uri/$id")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val propertyId = selectionArgs?.first()?.toLongOrNull()
            ?: throw IllegalArgumentException("propertyId must be provided for delete operation")

        val count: Int = when (uriMatcher.match(uri)) {
            PROPERTY -> propertyDao?.deleteBySelection(propertyId) ?: 0
            PHOTO -> photoDao.deleteBySelection(propertyId)
            POI -> pointOfInterestDao.deleteBySelection(propertyId)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val propertyId = selectionArgs?.first()?.toLongOrNull()
            ?: throw IllegalArgumentException("propertyId must be provided for update operation")

        var count = 0
        runBlocking {
            count = when (uriMatcher.match(uri)) {
                PROPERTY -> {
                    val property = propertyDao?.getById(propertyId)
                    property?.apply {
                        type = values?.getAsString("type") ?: type
                        address = values?.getAsString("address") ?: address
                    }
                    property?.let { propertyDao?.updateAsCursor(it) } ?: 0
                }

                else -> throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }


}