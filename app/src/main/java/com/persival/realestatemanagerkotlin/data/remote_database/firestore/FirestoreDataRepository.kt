package com.persival.realestatemanagerkotlin.data.remote_database.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    // -------------- Property Operations -------------- //

    fun addProperty(property: PropertyEntity): Task<DocumentReference> {
        return firestore.collection("properties").add(property)
    }

    fun getProperty(propertyId: String): Task<DocumentSnapshot> {
        return firestore.collection("properties").document(propertyId).get()
    }

    fun updateProperty(propertyId: String, property: PropertyEntity): Task<Void> {
        return firestore.collection("properties").document(propertyId).set(property)
    }

    fun deleteProperty(propertyId: String): Task<Void> {
        return firestore.collection("properties").document(propertyId).delete()
    }

    // -------------- Photo Operations -------------- //

    fun addPhoto(propertyId: String, photo: PhotoEntity): Task<DocumentReference> {
        return firestore.collection("properties").document(propertyId).collection("photos").add(photo)
    }

    fun getPhotos(propertyId: String): Task<QuerySnapshot> {
        return firestore.collection("properties").document(propertyId).collection("photos").get()
    }

    // It's likely you'll need more photo-related operations, such as updating or deleting.

    // -------------- Point Of Interest Operations -------------- //

    fun addPOI(propertyId: String, poi: PointOfInterestEntity): Task<DocumentReference> {
        return firestore.collection("properties").document(propertyId).collection("pointsOfInterest").add(poi)
    }

    fun getPOIs(propertyId: String): Task<QuerySnapshot> {
        return firestore.collection("properties").document(propertyId).collection("pointsOfInterest").get()
    }

    // Again, consider adding more operations for points of interest if needed.

    // -------------- Combined Operations -------------- //

    suspend fun getPropertyWithDetails(propertyId: String): PropertyWithPhotosAndPOIEntity? {
        val propertySnapshot = getProperty(propertyId).await()
        val property = propertySnapshot.toObject(PropertyEntity::class.java) ?: return null

        val photosSnapshot = getPhotos(propertyId).await()
        val photos = photosSnapshot.documents.mapNotNull { it.toObject(PhotoEntity::class.java) }

        val poisSnapshot = getPOIs(propertyId).await()
        val pois = poisSnapshot.documents.mapNotNull { it.toObject(PointOfInterestEntity::class.java) }

        return PropertyWithPhotosAndPOIEntity(property, photos, pois)
    }

}


