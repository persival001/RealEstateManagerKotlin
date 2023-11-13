package com.persival.realestatemanagerkotlin.domain.permissions

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Before
import org.junit.Test

class IsGpsActivatedUseCaseTest {

    private val permissionRepository: PermissionRepository = mockk()

    private lateinit var isGpsActivatedUseCase: IsGpsActivatedUseCase

    @Before
    fun setUp() {
        isGpsActivatedUseCase = IsGpsActivatedUseCase(permissionRepository)
    }

    @Test
    fun `invoke emits true when GPS is activated`() = runTest {
        every { permissionRepository.isGpsActivated() } returns flowOf(true)

        val result = isGpsActivatedUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(true))
    }

    @Test
    fun `invoke emits false when GPS is deactivated`() = runTest {
        every { permissionRepository.isGpsActivated() } returns flowOf(false)

        val result = isGpsActivatedUseCase.invoke().toList()

        assertThat(result).isEqualTo(listOf(false))
    }
}
