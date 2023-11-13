package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetAllPropertiesWithPhotosAndPOIUseCaseTest {

    private val localRepository = mockk<LocalRepository>()
    private val getAllPropertiesWithPhotosAndPOIUseCase = GetAllPropertiesWithPhotosAndPOIUseCase(localRepository)

    /*@Test
    fun `invoke returns list of properties with photos and poi successfully`() = runBlocking {
        // Arrange
        val expectedList = listOf(PropertyWithPhotosAndPOIEntity(...))
        coEvery { localRepository.getAllProperties() } returns flowOf(expectedList)

        // Act
        val result = getAllPropertiesWithPhotosAndPOIUseCase.invoke().first()

        // Assert
        assertThat(result).isEqualTo(expectedList)
    }*/

    @Test
    fun `invoke handles errors when retrieving properties with photos and poi`() = runBlocking {
        // Arrange
        val exception = RuntimeException("Erreur de test")
        coEvery { localRepository.getAllProperties() } throws exception

        // Act & Assert
        assertThat { getAllPropertiesWithPhotosAndPOIUseCase.invoke().first() }
            .isFailure()
            .isEqualTo(exception)
    }
}
