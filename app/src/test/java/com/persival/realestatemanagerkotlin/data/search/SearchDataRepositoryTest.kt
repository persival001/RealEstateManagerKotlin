package com.persival.realestatemanagerkotlin.data.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isNull
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchDataRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var searchDataRepository: SearchDataRepository

    @Before
    fun setUp() {
        searchDataRepository = SearchDataRepository()
    }

    @Test
    fun `selectedFilter emits correct initial value`() = testCoroutineRule.runTest {
        assertThat(searchDataRepository.selectedFilter.first()).isNull()
    }

    /*@Test
    fun `selectedFilter updates when setFilter is called with a non-null value`() =
        testCoroutineRule.runTest {
            val searchEntity = SearchEntity(
                type = "Appartement",
                minPrice = 50000,
                maxPrice = 250000,
                minArea = 30,
                maxArea = 100,
                pois = "Écoles, Métro",
                date = "2023-04-01"
            )

            // Act
            searchDataRepository.setFilter(searchEntity)

            // Assert
            assertEquals(searchEntity, searchDataRepository.selectedFilter.first())
        }*/

    @Test
    fun `selectedFilter updates when setFilter is called with a null value`() =
        testCoroutineRule.runTest {
            // Act
            searchDataRepository.setFilter(null)

            // Assert
            assertThat(searchDataRepository.selectedFilter.first()).isNull()
        }

    /*@Test
    fun `selectedFilter updates with various SearchEntity instances`() = testCoroutineRule.runTest {
        val testCases = listOf(
            SearchEntity("Maison", 200000, 800000, 100, 300, "Parcs, Hôpital", "2023-05-10"),
            SearchEntity("Studio", 10000, 150000, 20, 50, "Commerces, Gym", "2023-06-15"),
        )

        // Prepare a list to collect the emissions
        val emissions = mutableListOf<SearchEntity?>()

        // Launch a coroutine to collect the flow emissions
        val job = launch {
            searchDataRepository.selectedFilter.toList(emissions)
        }

        // Set the filters and wait for values to be emitted
        testCases.forEach { searchEntity ->
            searchDataRepository.setFilter(searchEntity)
            // Give some time for the value to be emitted
            advanceTimeBy(100)
        }

        // Cleanup and cancel the job after collecting the emissions
        job.cancel()

        // Assert that all test cases are in the emissions
        assertTrue(emissions.containsAll(testCases))
    }*/

}
