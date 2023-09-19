package com.persival.realestatemanagerkotlin.data.remote_database.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDto
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDto
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDto
import com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois.PropertyWithPhotosAndPoisDto
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    companion object {
        private const val PROPERTIES = "properties"
        private const val PHOTOS = "photos"
        private const val POINTS_OF_INTEREST = "pointsOfInterest"
        private const val LAST_MODIFIED = "lastModified"
    }

    // -------------- Property Operations -------------- //

    fun addProperty(property: PropertyDto): Task<DocumentReference> = firestore.collection(PROPERTIES).add(property)

    private fun getProperty(propertyId: Long): Task<DocumentSnapshot> =
        firestore.collection(PROPERTIES).document(propertyId.toString()).get()

    // -------------- Photo Operations -------------- //

    fun addPhoto(propertyId: Long, photo: PhotoDto): Task<DocumentReference> {
        return firestore.collection(PROPERTIES).document(propertyId.toString()).collection(PHOTOS).add(photo)
    }

    fun getPhotos(propertyId: Long): Task<QuerySnapshot> {
        return firestore.collection(PROPERTIES).document(propertyId.toString()).collection(PHOTOS).get()
    }

    // -------------- Point Of Interest Operations -------------- //

    fun addPOI(propertyId: Long, poi: PointOfInterestDto): Task<DocumentReference> {
        return firestore.collection(PROPERTIES).document(propertyId.toString()).collection(POINTS_OF_INTEREST)
            .add(poi)
    }

    fun getPOIs(propertyId: Long): Task<QuerySnapshot> {
        return firestore.collection(PROPERTIES).document(propertyId.toString()).collection(POINTS_OF_INTEREST).get()
    }

    // -------------- Combined Operations -------------- //

    suspend fun getPropertyWithDetails(propertyId: Long): PropertyWithPhotosAndPoisDto? {
        val propertySnapshot = getProperty(propertyId).await()
        val property = propertySnapshot.toObject(PropertyDto::class.java) ?: return null

        val photosSnapshot = getPhotos(propertyId).await()
        val photos = photosSnapshot.documents.mapNotNull { it.toObject(PhotoDto::class.java) }

        val poisSnapshot = getPOIs(propertyId).await()
        val pois = poisSnapshot.documents.mapNotNull { it.toObject(PointOfInterestDto::class.java) }

        return PropertyWithPhotosAndPoisDto(property, photos, pois)
    }

    suspend fun getUpdatedPropertiesSince(dateTime: LocalDateTime): List<PropertyDto> {
        val lastSyncTimestamp = Timestamp(dateTime.toEpochSecond(ZoneOffset.UTC), dateTime.nano)
        val snapshots = firestore.collection(PROPERTIES)
            .whereGreaterThan(LAST_MODIFIED, lastSyncTimestamp)
            .get()
            .await()
        return snapshots.documents.mapNotNull { it.toObject(PropertyDto::class.java) }
    }

    suspend fun getUpdatedPhotosSince(propertyId: Long, dateTime: LocalDateTime): List<PhotoDto> {
        val lastSyncTimestamp = Timestamp(dateTime.toEpochSecond(ZoneOffset.UTC), dateTime.nano)
        val snapshots = firestore.collection(PROPERTIES)
            .document(propertyId.toString())
            .collection(PHOTOS)
            .whereGreaterThan(LAST_MODIFIED, lastSyncTimestamp)
            .get()
            .await()
        return snapshots.documents.mapNotNull { it.toObject(PhotoDto::class.java) }
    }

    suspend fun getUpdatedPointsOfInterestSince(propertyId: Long, dateTime: LocalDateTime): List<PointOfInterestDto> {
        val lastSyncTimestamp = Timestamp(dateTime.toEpochSecond(ZoneOffset.UTC), dateTime.nano)
        val snapshots = firestore.collection(PROPERTIES)
            .document(propertyId.toString())
            .collection(POINTS_OF_INTEREST)
            .whereGreaterThan(LAST_MODIFIED, lastSyncTimestamp)
            .get()
            .await()
        return snapshots.documents.mapNotNull { it.toObject(PointOfInterestDto::class.java) }
    }

}