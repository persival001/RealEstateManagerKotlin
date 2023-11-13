package com.persival.realestatemanagerkotlin.domain.permissions

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Before
import org.junit.Test

class HasCameraPermissionUseCaseTest {

    private val permissionRepository: PermissionRepository = mockk()

    private lateinit var hasCameraPermissionUseCase: HasCameraPermissionUseCase

    @Before
    fun setUp() {
        hasCameraPermissionUseCase = HasCameraPermissionUseCase(permissionRepository)
    }

    @Test
    fun `invoke emits true when camera permission is granted`() = runTest {
        every { permissionRepository.isCameraPermission() } returns flowOf(true)

        val result = hasCameraPermissionUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(true))
    }

    @Test
    fun `invoke emits false when camera permission is denied`() = runTest {
        every { permissionRepository.isCameraPermission() } returns flowOf(false)

        val result = hasCameraPermissionUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(false))
    }
}
