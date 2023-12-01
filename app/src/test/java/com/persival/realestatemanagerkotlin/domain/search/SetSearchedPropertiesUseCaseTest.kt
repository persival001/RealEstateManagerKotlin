package com.persival.realestatemanagerkotlin.domain.search

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SetSearchedPropertiesUseCaseTest {

    private lateinit var searchRepository: SearchRepository
    private lateinit var setSearchedPropertiesUseCase: SetSearchedPropertiesUseCase

    @Before
    fun setUp() {
        searchRepository = mockk(relaxed = true)

        setSearchedPropertiesUseCase = SetSearchedPropertiesUseCase(searchRepository)
    }

    @Test
    fun `invoke calls setFilter with correct search entity`() {
        val searchEntity = mockk<SearchEntity>(relaxed = true)

        setSearchedPropertiesUseCase.invoke(searchEntity)

        verify { searchRepository.setFilter(searchEntity) }
    }
}

