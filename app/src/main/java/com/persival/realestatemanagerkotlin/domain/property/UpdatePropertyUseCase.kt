package com.persival.realestatemanagerkotlin.domain.property

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePropertyUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    suspend fun invoke(propertyEntity: PropertyEntity) {
        localRepository.updateProperty(propertyEntity)
    }
}