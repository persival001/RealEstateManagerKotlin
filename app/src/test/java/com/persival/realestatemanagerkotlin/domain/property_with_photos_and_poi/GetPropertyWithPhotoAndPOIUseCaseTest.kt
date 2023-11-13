package com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetPropertyWithPhotoAndPOIUseCaseTest {

    private val localRepository = mockk<LocalRepository>()
    private val getPropertyWithPhotoAndPOIUseCase = GetPropertyWithPhotoAndPOIUseCase(localRepository)

    /* @Test
     fun `invoke returns property with photos and poi successfully`() = runBlocking {
         // Arrange
         val propertyId = 1L
         val expectedProperty = PropertyWithPhotosAndPOIEntity(...) // Remplacer avec des données de test appropriées
         coEvery { localRepository.getPropertyById(propertyId) } returns flow { emit(expectedProperty) }

         // Act
         val result = getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).first()

         // Assert
         assertThat(result).isEqualTo(expectedProperty)
     }*/

    @Test
    fun `invoke handles errors when retrieving property with photos and poi`() = runBlocking {
        // Arrange
        val propertyId = 1L
        val exception = RuntimeException("Erreur de test")
        coEvery { localRepository.getPropertyById(propertyId) } returns flow { throw exception }

        // Act & Assert
        assertThat { getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).first() }
            .isFailure()
            .isEqualTo(exception)
    }
}
