package com.persival.realestatemanagerkotlin.domain.property

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.persival.realestatemanagerkotlin.domain.property.model.Property
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class InsertPropertyUseCaseTest {

    private lateinit var localRepository: LocalRepository
    private lateinit var insertPropertyUseCase: InsertPropertyUseCase

    @Before
    fun setUp() {
        localRepository = mockk()
        insertPropertyUseCase = InsertPropertyUseCase(localRepository)
    }

    @Test
    fun `invoke returns new property id on successful insert`() = runBlocking {
        val property = Property(
            id = 0L,
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
        val expectedId = 1L
        coEvery { localRepository.insertProperty(property) } returns expectedId

        val result = insertPropertyUseCase.invoke(property)

        assertThat(result).isEqualTo(expectedId)
    }

    @Test
    fun `invoke returns null on insert failure`() = runBlocking {
        val property = Property(
            id = 0L,
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
        coEvery { localRepository.insertProperty(property) } returns null

        val result = insertPropertyUseCase.invoke(property)

        assertThat(result).isNull()
    }
}
