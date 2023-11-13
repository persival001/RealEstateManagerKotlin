package com.persival.realestatemanagerkotlin.domain.permissions

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Before
import org.junit.Test

class HasStoragePermissionUseCaseTest {

    private val permissionRepository: PermissionRepository = mockk()

    private lateinit var hasStoragePermissionUseCase: HasStoragePermissionUseCase

    @Before
    fun setUp() {
        hasStoragePermissionUseCase = HasStoragePermissionUseCase(permissionRepository)
    }

    @Test
    fun `invoke emits true when storage read images permission is granted`() = runTest {
        coEvery { permissionRepository.isStorageReadImagesPermission() } returns flowOf(true)

        val result = hasStoragePermissionUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(true))
    }

    @Test
    fun `invoke emits false when storage read images permission is denied`() = runTest {
        coEvery { permissionRepository.isStorageReadImagesPermission() } returns flowOf(false)

        val result = hasStoragePermissionUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(false))
    }
}
