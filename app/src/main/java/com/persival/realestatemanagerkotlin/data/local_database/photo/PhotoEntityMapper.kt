package com.persival.realestatemanagerkotlin.data.local_database.photo

import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
import javax.inject.Inject

class PhotoEntityMapper @Inject constructor() {

    fun mapPhotoDtoToPhotoEntity(photoEntity: PhotoEntity): Photo {
        return Photo(
            id = photoEntity.id,
            propertyId = photoEntity.propertyId,
            description = photoEntity.description,
            photoUrl = photoEntity.photoUrl,
        )
    }

    fun mapPhotoEntityToPhotoDto(photo: Photo): PhotoEntity {
        return PhotoEntity(
            id = photo.id,
            propertyId = photo.propertyId,
            description = photo.description,
            photoUrl = photo.photoUrl,
        )
    }
}
