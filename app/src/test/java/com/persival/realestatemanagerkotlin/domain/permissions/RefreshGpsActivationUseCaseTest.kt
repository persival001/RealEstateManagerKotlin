package com.persival.realestatemanagerkotlin.domain.permissions

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class RefreshGpsActivationUseCaseTest {

    private val permissionRepository: PermissionRepository = mockk(relaxUnitFun = true)

    private lateinit var refreshGpsActivationUseCase: RefreshGpsActivationUseCase

    @Before
    fun setUp() {
        refreshGpsActivationUseCase = RefreshGpsActivationUseCase(permissionRepository)
    }

    @Test
    fun `invoke calls refreshGpsActivation on repository`() {
        refreshGpsActivationUseCase.invoke()

        verify(exactly = 1) { permissionRepository.refreshGpsActivation() }
    }
}
