package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertPhotoUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend fun invoke(photo: Photo): Long? {
        return localRepository.insertPhoto(photo)
    }
}