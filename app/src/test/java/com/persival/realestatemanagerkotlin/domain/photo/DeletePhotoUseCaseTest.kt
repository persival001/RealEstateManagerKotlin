package com.persival.realestatemanagerkotlin.domain.photo

import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeletePhotoUseCaseTest {

    private val localRepository: LocalRepository = mockk()

    private lateinit var deletePhotoUseCase: DeletePhotoUseCase

    @Before
    fun setUp() {
        deletePhotoUseCase = DeletePhotoUseCase(localRepository)
    }

    @Test
    fun `invoke calls deletePhotoByPropertyIdAndPhotoId on repository`() = runTest {
        coEvery { localRepository.deletePhotoByPropertyIdAndPhotoId(any(), any()) } returns Unit

        deletePhotoUseCase.invoke(1L, 2L)

        coVerify(exactly = 1) { localRepository.deletePhotoByPropertyIdAndPhotoId(1L, 2L) }
    }
}
