package com.persival.realestatemanagerkotlin.data.remote_database.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.persival.realestatemanagerkotlin.data.local_database.model.PhotoDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PointOfInterestDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyDto
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyWithPhotosAndPoisDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    // -------------- Property Operations -------------- //

    fun addProperty(property: PropertyDto): Task<DocumentReference> {
        return firestore.collection("properties").add(property)
    }

    fun getProperty(propertyId: String): Task<DocumentSnapshot> {
        return firestore.collection("properties").document(propertyId).get()
    }

    fun updateProperty(propertyId: String, property: PropertyDto): Task<Void> {
        return firestore.collection("properties").document(propertyId).set(property)
    }

    fun deleteProperty(propertyId: String): Task<Void> {
        return firestore.collection("properties").document(propertyId).delete()
    }

    // -------------- Photo Operations -------------- //

    fun addPhoto(propertyId: String, photo: PhotoDto): Task<DocumentReference> {
        return firestore.collection("properties").document(propertyId).collection("photos").add(photo)
    }

    fun getPhotos(propertyId: String): Task<QuerySnapshot> {
        return firestore.collection("properties").document(propertyId).collection("photos").get()
    }

    // -------------- Point Of Interest Operations -------------- //

    fun addPOI(propertyId: String, poi: PointOfInterestDto): Task<DocumentReference> {
        return firestore.collection("properties").document(propertyId).collection("pointsOfInterest").add(poi)
    }

    fun getPOIs(propertyId: String): Task<QuerySnapshot> {
        return firestore.collection("properties").document(propertyId).collection("pointsOfInterest").get()
    }

    // Again, consider adding more operations for points of interest if needed.

    // -------------- Combined Operations -------------- //

    suspend fun getPropertyWithDetails(propertyId: String): PropertyWithPhotosAndPoisDto? {
        val propertySnapshot = getProperty(propertyId).await()
        val property = propertySnapshot.toObject(PropertyDto::class.java) ?: return null

        val photosSnapshot = getPhotos(propertyId).await()
        val photos = photosSnapshot.documents.mapNotNull { it.toObject(PhotoDto::class.java) }

        val poisSnapshot = getPOIs(propertyId).await()
        val pois = poisSnapshot.documents.mapNotNull { it.toObject(PointOfInterestDto::class.java) }

        return PropertyWithPhotosAndPoisDto(property, photos, pois)
    }

}


