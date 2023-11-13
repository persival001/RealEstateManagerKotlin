package com.persival.realestatemanagerkotlin.domain.location

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import com.persival.realestatemanagerkotlin.domain.permissions.HasLocationPermissionUseCase
import org.junit.Before
import org.junit.Test

class GetLocationUseCaseTest {

    private val hasLocationPermissionUseCase: HasLocationPermissionUseCase = mockk()
    private val locationRepository: LocationRepository = mockk()

    private lateinit var getLocationUseCase: GetLocationUseCase

    @Before
    fun setUp() {
        getLocationUseCase = GetLocationUseCase(hasLocationPermissionUseCase, locationRepository)
    }

    @Test
    fun `invoke emits location when permission is granted`() = runTest {

        val testLocation = LocationEntity(0.0, 0.0)

        every { hasLocationPermissionUseCase.invoke() } returns flowOf(true)
        every { locationRepository.getLocationFlow() } returns flowOf(testLocation)

        val result = getLocationUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(testLocation))
    }

    @Test
    fun `invoke emits nothing when permission is denied`() = runTest {
        every { hasLocationPermissionUseCase.invoke() } returns flowOf(false)
        every { locationRepository.getLocationFlow() } returns flowOf(LocationEntity(0.0, 0.0))

        val result = getLocationUseCase.invoke().toList()

        assertThat(result).isEqualTo(emptyList())
    }
}
