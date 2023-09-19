package com.persival.realestatemanagerkotlin.data.local_database.photo

import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import javax.inject.Inject

class PhotoDtoMapper @Inject constructor() {

    fun mapToEntity(model: PhotoDto): PhotoEntity {
        return PhotoEntity(
            id = model.id,
            propertyId = model.propertyId,
            description = model.description,
            photoUrl = model.photoUrl,
        )
    }

    fun mapFromDomainModel(domainModel: PhotoEntity): PhotoDto {
        return PhotoDto(
            id = domainModel.id ?: 0L,
            propertyId = domainModel.propertyId,
            description = domainModel.description,
            photoUrl = domainModel.photoUrl,
        )
    }
}
