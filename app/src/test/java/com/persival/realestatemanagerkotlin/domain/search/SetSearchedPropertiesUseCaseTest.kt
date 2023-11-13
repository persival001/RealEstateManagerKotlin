package com.persival.realestatemanagerkotlin.domain.search

import io.mockk.mockk

class SetSearchedPropertiesUseCaseTest {

    private val searchRepository = mockk<SearchRepository>()
    private val setSearchedPropertiesUseCase = SetSearchedPropertiesUseCase(searchRepository)

    /*@Test
    fun `invoke calls setFilter with correct search entity`() {
        // Arrange
        val searchEntity = SearchEntity(...) // Créez une instance de SearchEntity avec des données de test

        every { searchRepository.setFilter(searchEntity) } just runs

        // Act
        setSearchedPropertiesUseCase.invoke(searchEntity)

        // Assert
        verify { searchRepository.setFilter(searchEntity) }
    }*/
}
