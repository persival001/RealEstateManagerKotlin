package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class InsertPhotoUseCaseTest {

    private val localRepository: LocalRepository = mockk()

    private lateinit var insertPhotoUseCase: InsertPhotoUseCase

    @Before
    fun setUp() {
        insertPhotoUseCase = InsertPhotoUseCase(localRepository)
    }

    @Test
    fun `invoke inserts photoEntity and returns id`() = runTest {
        val photoEntity: PhotoEntity = mockk()
        val expectedId = 1L

        coEvery { localRepository.insertPhoto(photoEntity) } returns expectedId

        val resultId = insertPhotoUseCase.invoke(photoEntity)

        assertEquals(expectedId, resultId)

        coVerify(exactly = 1) { localRepository.insertPhoto(photoEntity) }
    }
}
