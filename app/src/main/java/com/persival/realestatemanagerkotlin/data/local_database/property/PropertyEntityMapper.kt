package com.persival.realestatemanagerkotlin.data.local_database.property

import com.persival.realestatemanagerkotlin.domain.property.model.Property
import javax.inject.Inject


class PropertyEntityMapper @Inject constructor() {

    fun mapPropertyDtoToPropertyEntity(propertyEntity: PropertyEntity) = Property(
        id = propertyEntity.id,
        type = propertyEntity.type,
        address = propertyEntity.address,
        latLng = propertyEntity.latLng,
        area = propertyEntity.area,
        rooms = propertyEntity.rooms,
        bathrooms = propertyEntity.bathrooms,
        bedrooms = propertyEntity.bedrooms,
        description = propertyEntity.description,
        price = propertyEntity.price,
        isSold = propertyEntity.isSold,
        entryDate = propertyEntity.entryDate,
        saleDate = propertyEntity.saleDate,
        agentName = propertyEntity.agentName
    )

    fun mapPropertyEntityToPropertyDto(property: Property) = PropertyEntity(
        id = property.id,
        type = property.type,
        address = property.address,
        latLng = property.latLng,
        area = property.area,
        rooms = property.rooms,
        bathrooms = property.bathrooms,
        bedrooms = property.bedrooms,
        description = property.description,
        price = property.price,
        isSold = property.isSold,
        entryDate = property.entryDate,
        saleDate = property.saleDate,
        agentName = property.agentName,
        lastModified = System.currentTimeMillis(),
        isSynced = false
    )
}
