package com.persival.realestatemanagerkotlin.domain.point_of_interest

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class UpdatePointOfInterestUseCaseTest {

    private lateinit var localRepository: LocalRepository
    private lateinit var updatePointOfInterestUseCase: UpdatePointOfInterestUseCase

    @Before
    fun setUp() {
        localRepository = mockk()
        updatePointOfInterestUseCase = UpdatePointOfInterestUseCase(localRepository)
    }

    @Test
    fun `invoke calls repository to update points of interest`() = runBlocking {
        val propertyId = 1L
        val pointOfInterestEntities = listOf<PointOfInterestEntity>()

        coEvery { localRepository.updatePointOfInterestWithPropertyId(propertyId, pointOfInterestEntities) } returns Unit

        updatePointOfInterestUseCase.invoke(propertyId, pointOfInterestEntities)

        coVerify(exactly = 1) { localRepository.updatePointOfInterestWithPropertyId(propertyId, pointOfInterestEntities) }
    }

}
