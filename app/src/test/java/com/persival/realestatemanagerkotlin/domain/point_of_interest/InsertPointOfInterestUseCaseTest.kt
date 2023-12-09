package com.persival.realestatemanagerkotlin.domain.point_of_interest

import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class InsertPointOfInterestUseCaseTest {

    private lateinit var localRepository: LocalRepository
    private lateinit var insertPointOfInterestUseCase: InsertPointOfInterestUseCase

    @Before
    fun setUp() {
        localRepository = mockk()

        insertPointOfInterestUseCase = InsertPointOfInterestUseCase(localRepository)
    }

    @Test
    fun `invoke returns new id on successful insertion`() = runTest {
        val pointOfInterest: PointOfInterest = mockk()
        val expectedId = 1L

        coEvery { localRepository.insertPointOfInterest(pointOfInterest) } returns expectedId

        val resultId = insertPointOfInterestUseCase.invoke(pointOfInterest)

        coVerify(exactly = 1) { localRepository.insertPointOfInterest(pointOfInterest) }

        assertEquals(expectedId, resultId)
    }

    @Test
    fun `invoke returns null on insertion failure`() = runTest {
        val pointOfInterest: PointOfInterest = mockk()

        coEvery { localRepository.insertPointOfInterest(pointOfInterest) } returns null

        val resultId = insertPointOfInterestUseCase.invoke(pointOfInterest)

        coVerify(exactly = 1) { localRepository.insertPointOfInterest(pointOfInterest) }

        assertNull(resultId)
    }
}

