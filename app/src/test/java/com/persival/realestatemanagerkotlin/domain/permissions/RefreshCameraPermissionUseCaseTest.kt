package com.persival.realestatemanagerkotlin.domain.permissions

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class RefreshCameraPermissionUseCaseTest {

    private val permissionRepository: PermissionRepository = mockk(relaxUnitFun = true)

    private lateinit var refreshCameraPermissionUseCase: RefreshCameraPermissionUseCase

    @Before
    fun setUp() {
        refreshCameraPermissionUseCase = RefreshCameraPermissionUseCase(permissionRepository)
    }

    @Test
    fun `invoke calls refreshCameraPermission on repository`() {
        refreshCameraPermissionUseCase.invoke()

        verify(exactly = 1) { permissionRepository.refreshCameraPermission() }
    }
}
