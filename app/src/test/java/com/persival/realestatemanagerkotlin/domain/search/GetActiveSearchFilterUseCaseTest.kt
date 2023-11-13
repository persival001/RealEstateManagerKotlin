package com.persival.realestatemanagerkotlin.domain.search

import io.mockk.mockk

class GetActiveSearchFilterUseCaseTest {

    private val searchRepository = mockk<SearchRepository>()
    private val getActiveSearchFilterUseCase = GetActiveSearchFilterUseCase(searchRepository)

    /* @Test
     fun `invoke returns current search filter`() = runTest {
         // Arrange
         val expectedSearchFilter = SearchEntity(...) // Remplacer avec les données de test appropriées
         val stateFlow = MutableStateFlow(expectedSearchFilter)
         every { searchRepository.selectedFilter } returns stateFlow

         // Act
         val result = getActiveSearchFilterUseCase.invoke().first()

         // Assert
         assertThat(result).isEqualTo(expectedSearchFilter)
     }*/
}
