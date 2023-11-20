package com.persival.realestatemanagerkotlin.domain.search

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
            entryDate = params.entryDate,
            poi = params.poi
        ).map { properties ->
            if (params.entryDate == null) {
                properties
            } else {
                properties.filter { propertyWithPhotosAndPOI ->
                    val entryDateProperty =
                        LocalDate.parse(propertyWithPhotosAndPOI.property.entryDate, DateTimeFormatter.ISO_LOCAL_DATE)
                    when (params.entryDate) {
                        1.toString() -> entryDateProperty.isAfter(LocalDate.now().minusMonths(1))
                        2.toString() -> entryDateProperty.isAfter(
                            LocalDate.now().minusMonths(6)
                        ) && entryDateProperty.isBefore(
                            LocalDate.now().minusMonths(1)
                        )

                        3.toString() -> entryDateProperty.isBefore(LocalDate.now().minusMonths(6))
                        else -> true
                    }
                }
            }
        }
}