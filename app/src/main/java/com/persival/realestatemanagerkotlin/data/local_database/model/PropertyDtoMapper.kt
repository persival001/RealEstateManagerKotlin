package com.persival.realestatemanagerkotlin.data.local_database.model

import com.persival.realestatemanagerkotlin.data.local_database.util.DomainMapper
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity


class PropertyDtoMapper : DomainMapper<PropertyDto, PropertyEntity> {

    override fun mapToEntity(model: PropertyDto) = PropertyEntity(
        id = model.id,
        type = model.type,
        address = model.address,
        latLng = model.latLng,
        area = model.area,
        rooms = model.rooms,
        bathrooms = model.bathrooms,
        bedrooms = model.bedrooms,
        description = model.description,
        price = model.price,
        isSold = model.isSold,
        entryDate = model.entryDate,
        saleDate = model.saleDate,
        agentName = model.agentName
    )

    override fun mapFromDomainModel(domainModel: PropertyEntity) = PropertyDto(
        id = domainModel.id ?: 0L,
        type = domainModel.type,
        address = domainModel.address,
        latLng = domainModel.latLng,
        area = domainModel.area,
        rooms = domainModel.rooms,
        bathrooms = domainModel.bathrooms,
        bedrooms = domainModel.bedrooms,
        description = domainModel.description,
        price = domainModel.price,
        isSold = domainModel.isSold,
        entryDate = domainModel.entryDate,
        saleDate = domainModel.saleDate,
        agentName = domainModel.agentName
    )
}
