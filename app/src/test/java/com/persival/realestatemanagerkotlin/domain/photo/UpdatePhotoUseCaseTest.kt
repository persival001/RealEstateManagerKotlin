package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
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
        val photo: Photo = mockk()
        val rowsUpdated = 1

        coEvery { localRepository.updatePhoto(photo) } returns rowsUpdated

        updatePhotoUseCase.invoke(photo)

        coVerify(exactly = 1) { localRepository.updatePhoto(photo) }
    }
}

