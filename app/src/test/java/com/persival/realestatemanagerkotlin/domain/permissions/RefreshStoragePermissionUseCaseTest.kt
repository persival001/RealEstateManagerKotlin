package com.persival.realestatemanagerkotlin.domain.permissions

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class RefreshStoragePermissionUseCaseTest {

    private val permissionRepository: PermissionRepository = mockk(relaxUnitFun = true)

    private lateinit var refreshStoragePermissionUseCase: RefreshStoragePermissionUseCase

    @Before
    fun setUp() {
        refreshStoragePermissionUseCase = RefreshStoragePermissionUseCase(permissionRepository)
    }

    @Test
    fun `invoke calls refreshStorageReadImagesPermission on repository`() {
        refreshStoragePermissionUseCase.invoke()

        verify(exactly = 1) { permissionRepository.refreshStorageReadImagesPermission() }
    }
}
