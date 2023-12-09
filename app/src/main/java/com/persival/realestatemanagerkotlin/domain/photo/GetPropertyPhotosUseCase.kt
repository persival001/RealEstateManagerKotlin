package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPropertyPhotosUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    fun invoke(propertyId: Long): Flow<List<Photo>> {
        return localRepository.getPropertyPhotos(propertyId)
    }
}
