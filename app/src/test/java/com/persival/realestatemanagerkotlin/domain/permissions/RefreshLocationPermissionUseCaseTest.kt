package com.persival.realestatemanagerkotlin.domain.permissions

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class RefreshLocationPermissionUseCaseTest {

    private val permissionRepository: PermissionRepository = mockk(relaxUnitFun = true)

    private lateinit var refreshLocationPermissionUseCase: RefreshLocationPermissionUseCase

    @Before
    fun setUp() {
        refreshLocationPermissionUseCase = RefreshLocationPermissionUseCase(permissionRepository)
    }

    @Test
    fun `invoke calls refreshLocationPermission on repository`() {
        refreshLocationPermissionUseCase.invoke()

        verify(exactly = 1) { permissionRepository.refreshLocationPermission() }
    }
}
