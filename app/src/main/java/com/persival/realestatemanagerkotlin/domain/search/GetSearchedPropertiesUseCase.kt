package com.persival.realestatemanagerkotlin.domain.search

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSearchedPropertiesUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun invoke(params: SearchEntity): Flow<List<PropertyWithPhotosAndPOIEntity>> =
        localRepository.getSearchedPropertiesWithPOIs(
            type = params.type,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice,
            minArea = params.minArea,
            maxArea = params.maxArea,
            isSold = params.isSold,
            timeFilter = params.timeFilter,
            poiSchool = if (params.poi.contains("School")) "School" else null,
            poiRestaurant = if (params.poi.contains("Restaurant")) "Restaurant" else null,
        )

}