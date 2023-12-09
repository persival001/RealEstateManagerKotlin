package com.persival.realestatemanagerkotlin.domain.property

import com.persival.realestatemanagerkotlin.domain.property.model.Property
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class UpdatePropertyUseCaseTest {

    private lateinit var localRepository: LocalRepository
    private lateinit var updatePropertyUseCase: UpdatePropertyUseCase

    @Before
    fun setUp() {
        localRepository = mockk()
        updatePropertyUseCase = UpdatePropertyUseCase(localRepository)
    }

    @Test
    fun `invoke updates property successfully`() = runBlocking {
        val property = Property(
            id = 1L,
            type = "Maison",
            address = "123 rue de Exemple",
            latLng = "45.5017,-73.5673",
            area = 120,
            rooms = 5,
            bathrooms = 2,
            bedrooms = 3,
            description = "Belle maison en centre-ville",
            price = 250000,
            isSold = false,
            entryDate = "-1 month",
            saleDate = null,
            agentName = "John Doe"
        )
        coEvery { localRepository.updateProperty(property) } returns 1

        updatePropertyUseCase.invoke(property)

        coVerify { localRepository.updateProperty(property) }
    }

}
