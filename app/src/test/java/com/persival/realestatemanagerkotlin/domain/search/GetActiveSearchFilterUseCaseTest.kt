package com.persival.realestatemanagerkotlin.domain.search

import com.persival.realestatemanagerkotlin.domain.search.model.Search
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetActiveSearchFilterUseCaseTest {

    private lateinit var searchRepository: SearchRepository
    private lateinit var getActiveSearchFilterUseCase: GetActiveSearchFilterUseCase

    @Before
    fun setUp() {
        searchRepository = mockk()
        getActiveSearchFilterUseCase = GetActiveSearchFilterUseCase(searchRepository)
    }

    @Test
    fun `invoke returns StateFlow of SearchEntity from repository`() = runTest {
        val expectedFlow: StateFlow<Search?> = flowOf(mockk<Search>()).stateIn(this)

        every { searchRepository.selectedFilter } returns expectedFlow

        val result = getActiveSearchFilterUseCase.invoke()

        assertEquals(expectedFlow, result)
    }
}

