package com.persival.realestatemanagerkotlin.domain.permissions

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Before
import org.junit.Test

class HasLocationPermissionUseCaseTest {

    private val permissionRepository: PermissionRepository = mockk()

    private lateinit var hasLocationPermissionUseCase: HasLocationPermissionUseCase

    @Before
    fun setUp() {
        hasLocationPermissionUseCase = HasLocationPermissionUseCase(permissionRepository)
    }

    @Test
    fun `invoke emits true when location permission is granted`() = runTest {
        every { permissionRepository.isLocationPermission() } returns flowOf(true)

        val result = hasLocationPermissionUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(true))
    }

    @Test
    fun `invoke emits false when location permission is denied`() = runTest {
        every { permissionRepository.isLocationPermission() } returns flowOf(false)

        val result = hasLocationPermissionUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(false))
    }
}
