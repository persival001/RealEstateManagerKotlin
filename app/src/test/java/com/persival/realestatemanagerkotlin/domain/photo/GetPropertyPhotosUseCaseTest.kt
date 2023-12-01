package com.persival.realestatemanagerkotlin.domain.photo

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetPropertyPhotosUseCaseTest {

    private val localRepository: LocalRepository = mockk()

    private lateinit var getPropertyPhotosUseCase: GetPropertyPhotosUseCase

    @Before
    fun setUp() {
        getPropertyPhotosUseCase = GetPropertyPhotosUseCase(localRepository)
    }

    @Test
    fun `invoke returns property photos flow from repository`() = runTest {
        val mockPhotoEntity = mockk<PhotoEntity>()
        val testFlow: Flow<List<PhotoEntity>> = flowOf(listOf(mockPhotoEntity))

        every { localRepository.getPropertyPhotos(any()) } returns testFlow

        val result = getPropertyPhotosUseCase.invoke(1L)

        val photosList = result.toList()

        assertThat(photosList).isNotNull()
        assertThat(photosList).isEqualTo(listOf(listOf(mockPhotoEntity)))

        verify { localRepository.getPropertyPhotos(1L) }
    }
}
