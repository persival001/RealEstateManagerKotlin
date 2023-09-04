package com.persival.realestatemanagerkotlin.data.local_database.content_provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class PropertyProvider : ContentProvider() {

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI(AUTHORITY, "properties", PROPERTY)
        uriMatcher.addURI(AUTHORITY, "photos", PHOTO)
        uriMatcher.addURI(AUTHORITY, "points_of_interest", POI)
        uriMatcher.addURI(AUTHORITY, "properties/#", PROPERTY_ID)
    }

    companion object {
        const val AUTHORITY =
            "com.persival.realestatemanagerkotlin.data.local_database.content_provider.PropertyProvider"

        private const val PROPERTY = 1
        private const val PHOTO = 2
        private const val POI = 3
        private const val PROPERTY_ID = 4
    }

    private lateinit var propertyDao: PropertyDao
    private lateinit var photoDao: PhotoDao
    private lateinit var pointOfInterestDao: PointOfInterestDao

    override fun onCreate(): Boolean {
        val appContext = context?.applicationContext ?: throw IllegalStateException()
        val hiltEntryPoint = EntryPointAccessors.fromApplication(appContext, ContentProviderEntryPoint::class.java)
        propertyDao = hiltEntryPoint.getPropertyDao()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor = when (uriMatcher.match(uri)) {
        PROPERTY -> propertyDao.getAllPropertiesAsCursor()
        PHOTO -> photoDao.getAllPhotosAsCursor()
        POI -> pointOfInterestDao.getAllPointsOfInterestAsCursor()

        PROPERTY_ID -> propertyDao.getPropertyByIdAsCursor(
            propertyId = uri.lastPathSegment?.toLongOrNull()
                ?: throw IllegalArgumentException("ID must be provided for the property")
        )

        else -> throw IllegalArgumentException("Unknown URI: $uri")
    }.apply {
        setNotificationUri(context?.contentResolver, uri)
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int =
        0

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ContentProviderEntryPoint {
        fun getPropertyDao(): PropertyDao
    }
}