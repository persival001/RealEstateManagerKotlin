package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import javax.inject.Singleton

@Singleton
class InsertPhotoUseCase @javax.inject.Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend fun invoke(photoEntity: PhotoEntity): Long {
        return localRepository.insertPhoto(photoEntity)
    }
}