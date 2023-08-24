package com.persival.realestatemanagerkotlin.data.synchronize_database

import android.content.Context
import android.content.SharedPreferences
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.data.remote_database.firestore.FirestoreDataRepository
import com.persival.realestatemanagerkotlin.domain.database.SyncRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSyncRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val propertyDao: PropertyDao,
    private val photoDao: PhotoDao,
    private val pointOfInterestDao: PointOfInterestDao,
    private val firestoreDataRepository: FirestoreDataRepository,
) : SyncRepository {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "sync_preferences"
        private const val LAST_SYNC_TIME_KEY = "last_sync_time"
    }

    override suspend fun synchronizeData() {
        synchronizeProperties()
        synchronizePhotos()
        synchronizePointsOfInterest()
    }

    private suspend fun synchronizeProperties() {
        // Recovery of unsynced Room items
        val unsyncedProperties = propertyDao.getUnsyncedProperties()
        for (property in unsyncedProperties) {
            firestoreDataRepository.addProperty(property)
            propertyDao.markAsSynced(property.id)
        }

        // Checking for Firestore updates
        val lastSyncTime = getLastSyncTime()
        val updatedProperties = firestoreDataRepository.getUpdatedPropertiesSince(lastSyncTime)
        for (property in updatedProperties) {
            propertyDao.insert(property)
        }

        updateLastSyncTime(LocalDateTime.now())
    }

    private suspend fun synchronizePhotos() {
        // Recovery of unsynced Room items
        val unsyncedPhotos = photoDao.getUnsyncedPhotos()
        for (photo in unsyncedPhotos) {
            firestoreDataRepository.addPhoto(photo.propertyId, photo)
            photoDao.markAsSynced(photo.id)
        }

        // Checking for Firestore updates
        val lastSyncTime = getLastSyncTime()
        for (photo in unsyncedPhotos) {
            val updatedPhotos = firestoreDataRepository.getUpdatedPhotosSince(photo.propertyId, lastSyncTime)
            for (updatedPhoto in updatedPhotos) {
                photoDao.insertOrUpdatePhotos(updatedPhoto)
            }
        }

        updateLastSyncTime(System.currentTimeMillis())
    }

    private suspend fun synchronizePointsOfInterest() {
        val unsyncedPointsOfInterest = pointOfInterestDao.getUnsyncedPointsOfInterest()
        for (poi in unsyncedPointsOfInterest) {
            firestoreDataRepository.addPOI(poi.propertyId, poi)
            pointOfInterestDao.markAsSynced(poi.id)
        }

        // Checking for Firestore updates
        val lastSyncTime = getLastSyncTime()

        for (poi in unsyncedPointsOfInterest) {
            val updatedPOIs = firestoreDataRepository.getUpdatedPointsOfInterestSince(poi.propertyId, lastSyncTime)
            for (updatedPOI in updatedPOIs) {
                pointOfInterestDao.insertOrUpdatePointsOfInterest(updatedPOI)
            }
        }

        updateLastSyncTime(System.currentTimeMillis())
    }

    private fun getLastSyncTime(): LocalDateTime = LocalDateTime.ofEpochSecond(
        sharedPreferences.getLong(LAST_SYNC_TIME_KEY, 0L),
        0,
        ZoneOffset.UTC
    )

    private fun updateLastSyncTime(dateTime: LocalDateTime) {
        sharedPreferences.edit().putLong(LAST_SYNC_TIME_KEY, dateTime.toEpochSecond(ZoneOffset.UTC)).apply()
    }
}