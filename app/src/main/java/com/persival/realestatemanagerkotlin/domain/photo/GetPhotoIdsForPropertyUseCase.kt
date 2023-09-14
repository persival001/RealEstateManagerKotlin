package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPhotoIdsForPropertyUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    fun invoke(propertyId: Long): Flow<List<Long>> {
        return localRepository.getPhotoIdsForProperty(propertyId)
    }
}
