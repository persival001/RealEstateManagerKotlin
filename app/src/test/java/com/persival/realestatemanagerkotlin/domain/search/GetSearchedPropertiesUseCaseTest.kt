package com.persival.realestatemanagerkotlin.domain.search

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model.PropertyWithPhotosAndPOI
import com.persival.realestatemanagerkotlin.domain.search.model.Search
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSearchedPropertiesUseCaseTest {

    private lateinit var localRepository: LocalRepository
    private lateinit var getSearchedPropertiesUseCase: GetSearchedPropertiesUseCase

    @Before
    fun setUp() {
        localRepository = mockk(relaxed = true)
        getSearchedPropertiesUseCase = GetSearchedPropertiesUseCase(localRepository)
    }

    @Test
    fun `invoke returns properties flow from repository`() = runTest {
        val search = mockk<Search>(relaxed = true)
        val properties = listOf(mockk<PropertyWithPhotosAndPOI>())
        val propertiesFlow: Flow<List<PropertyWithPhotosAndPOI>> = flowOf(properties)

        coEvery {
            localRepository.getSearchedPropertiesWithPOIs(
                type = any(),
                minPrice = any(),
                maxPrice = any(),
                minArea = any(),
                maxArea = any(),
                isSold = any(),
                timeFilter = any(),
                poiSchool = any(),
                poiRestaurant = any(),
                poiPublicTransport = any(),
                poiHospital = any(),
                poiStore = any(),
                poiGreenSpaces = any()
            )
        } returns propertiesFlow

        val result = getSearchedPropertiesUseCase.invoke(search).first()

        assertEquals(properties, result)
    }
}
