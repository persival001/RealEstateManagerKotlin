package com.persival.realestatemanagerkotlin.domain.search

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model.PropertyWithPhotosAndPOI
import com.persival.realestatemanagerkotlin.domain.search.model.Search
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSearchedPropertiesUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun invoke(params: Search): Flow<List<PropertyWithPhotosAndPOI>> =
        localRepository.getSearchedPropertiesWithPOIs(
            type = params.type,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice,
            minArea = params.minArea,
            maxArea = params.maxArea,
            isSold = params.isSold,
            timeFilter = params.timeFilter,
            poiSchool = params.getPoiTypeIfPresent(PointOfInterestType.SCHOOL),
            poiRestaurant = params.getPoiTypeIfPresent(PointOfInterestType.RESTAURANT),
            poiPublicTransport = params.getPoiTypeIfPresent(PointOfInterestType.PUBLIC_TRANSPORT),
            poiHospital = params.getPoiTypeIfPresent(PointOfInterestType.HOSPITAL),
            poiStore = params.getPoiTypeIfPresent(PointOfInterestType.STORE),
            poiGreenSpaces = params.getPoiTypeIfPresent(PointOfInterestType.GREEN_SPACES),
        )

    private fun Search.getPoiTypeIfPresent(poiType: PointOfInterestType): String? {
        return if (this.poi.contains(poiType.type)) poiType.type else null
    }

}