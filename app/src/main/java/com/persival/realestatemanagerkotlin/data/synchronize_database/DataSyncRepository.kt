package com.persival.realestatemanagerkotlin.data.synchronize_database

import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.data.remote_database.firestore.FirestoreDataRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSyncRepository @Inject constructor(
    private val propertyDao: PropertyDao,
    private val photoDao: PhotoDao,
    private val pointOfInterestDao: PointOfInterestDao,
    private val firestoreDataRepository: FirestoreDataRepository,
) {

    suspend fun synchronizeData() {
        synchronizeProperties()
        synchronizePhotos()
        synchronizePointsOfInterest()
    }

    private suspend fun synchronizeProperties() {
        // Recovery of unsynced Room items
        val unsyncedProperties = propertyDao.getUnsyncedProperties()
        for (property in unsyncedProperties) {
            firestoreDataRepository.addProperty(property).await()
            propertyDao.markAsSynced(property.id)
        }

        // Checking for Firestore updates
        val lastSyncTime = getLastSyncTime()
        val updatedProperties = firestoreDataRepository.getUpdatedPropertiesSince(lastSyncTime).await()
        for (property in updatedProperties) {
            propertyDao.insertOrUpdateProperty(property)
        }

        updateLastSyncTime(System.currentTimeMillis())
    }

    private suspend fun synchronizePhotos() {
        val unsyncedPhotos = photoDao.getUnsyncedPhotos()
        for (photo in unsyncedPhotos) {
            firestoreDataRepository.addPhoto(photo).await()
            photoDao.markAsSynced(photo.id)
        }

        val lastSyncTime = getLastSyncTime()
        val updatedPhotos = firestoreDataRepository.getUpdatedPhotosSince(lastSyncTime).await()
        for (photo in updatedPhotos) {
            photoDao.insertOrUpdatePhotos(photo)
        }

        updateLastSyncTime(System.currentTimeMillis())
    }

    private suspend fun synchronizePointsOfInterest() {
        val unsyncedPointsOfInterest = pointOfInterestDao.getUnsyncedPointsOfInterest()
        for (poi in unsyncedPointsOfInterest) {
            firestoreDataRepository.addPOI(poi).await()
            pointOfInterestDao.markAsSynced(poi.id)
        }

        val lastSyncTime = getLastSyncTime()
        val updatedPOIs = firestoreDataRepository.getUpdatedPointsOfInterestSince(lastSyncTime).await()
        for (poi in updatedPOIs) {
            pointOfInterestDao.insertOrUpdatePointsOfInterest(poi)
        }

        updateLastSyncTime(System.currentTimeMillis())
    }


    private suspend fun getLastSyncTime(): Long {
        return 0
        // TODO: Récupère le dernier temps de synchronisation, soit depuis les préférences partagées, soit une autre source
    }

    private suspend fun updateLastSyncTime(time: Long) {
        // TODO: Met à jour le dernier temps de synchronisation
    }

}