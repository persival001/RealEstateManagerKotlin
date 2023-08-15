package com.persival.realestatemanagerkotlin.domain.property

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllLatLngUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    fun invoke(): Flow<List<String>> = localRepository.getAllPropertiesLatLng()
}