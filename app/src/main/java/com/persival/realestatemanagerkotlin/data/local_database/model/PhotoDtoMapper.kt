package com.persival.realestatemanagerkotlin.data.local_database.model

import com.persival.realestatemanagerkotlin.data.local_database.util.DomainMapper
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity


class PhotoDtoMapper : DomainMapper<PhotoDto, PhotoEntity> {

    override fun mapToDomainModel(model: PhotoDto): PhotoEntity {
        return PhotoEntity(
            id = model.id,
            propertyId = model.propertyId,
            description = model.description,
            photoUrl = model.photoUrl,
        )
    }

    override fun mapFromDomainModel(domainModel: PhotoEntity): PhotoDto {
        return PhotoDto(
            id = domainModel.id ?: 0L,
            propertyId = domainModel.propertyId,
            description = domainModel.description,
            photoUrl = domainModel.photoUrl,
        )
    }

    fun fromEntityList(initial: List<PhotoDto>): List<PhotoEntity> {
        return initial.map { mapToDomainModel(it) }
    }

    fun toEntityList(initial: List<PhotoEntity>): List<PhotoDto> {
        return initial.map { mapFromDomainModel(it) }
    }


}
