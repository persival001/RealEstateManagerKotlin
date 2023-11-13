package com.persival.realestatemanagerkotlin.data.property

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import assertk.assertThat
import assertk.assertions.isNull
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PropertyDataRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var propertyDataRepository: PropertyDataRepository

    @Before
    fun setUp() {
        propertyDataRepository = PropertyDataRepository()
    }

    @Test
    fun `selectedId emits correct values`() = testCoroutineRule.runTest {
        // Define test cases with expected inputs and outputs
        val testCases = listOf(null, 1L, 42L, null)

        // Check each case
        for (testCase in testCases) {
            // Act
            propertyDataRepository.setSelectedId(testCase)

            // Assert
            val emittedValue = propertyDataRepository.selectedId.first()
            if (testCase == null) {
                assertThat(emittedValue).isNull()
            } else {
                assertEquals(testCase, emittedValue)
            }
        }
    }
}
