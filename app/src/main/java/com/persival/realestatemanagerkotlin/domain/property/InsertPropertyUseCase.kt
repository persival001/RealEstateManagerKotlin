package com.persival.realestatemanagerkotlin.domain.property

import com.persival.realestatemanagerkotlin.domain.property.model.Property
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertPropertyUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend fun invoke(property: Property): Long? = localRepository.insertProperty(property)
}