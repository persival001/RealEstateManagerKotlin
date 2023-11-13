package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdatePhotoUseCaseTest {

    private val localRepository: LocalRepository = mockk()

    private lateinit var updatePhotoUseCase: UpdatePhotoUseCase

    @Before
    fun setUp() {
        updatePhotoUseCase = UpdatePhotoUseCase(localRepository)
    }

    @Test
    fun `invoke updates photoEntity and returns number of rows updated`() = runTest {
        val photoEntity: PhotoEntity = mockk()
        val rowsUpdated = 1

        coEvery { localRepository.updatePhoto(photoEntity) } returns rowsUpdated

        updatePhotoUseCase.invoke(photoEntity)

        coVerify(exactly = 1) { localRepository.updatePhoto(photoEntity) }
    }
}

