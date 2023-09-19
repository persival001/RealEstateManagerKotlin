package com.persival.realestatemanagerkotlin.domain.database

import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncFirestoreToRoomUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val propertyDao: PropertyDao,
    private val photoDao: PhotoDao,
    private val pointOfInterestDao: PointOfInterestDao,
) {
    suspend operator fun invoke() {
        val propertiesFromFirestore = firestoreRepository.getAllProperties()
        propertyDao.insertAll(propertiesFromFirestore)

        propertiesFromFirestore.forEach { property ->
            val photosFromFirestore = firestoreRepository.getPhotos(property.id)
            photoDao.insertAll(photosFromFirestore)

            val poisFromFirestore = firestoreRepository.getPOIs(property.id)
            pointOfInterestDao.insertAll(poisFromFirestore)
        }
    }
}
