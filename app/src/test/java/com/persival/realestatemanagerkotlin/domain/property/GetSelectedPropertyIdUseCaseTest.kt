package com.persival.realestatemanagerkotlin.domain.property

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSelectedPropertyIdUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase

    @Before
    fun setUp() {
        propertyRepository = mockk()
        getSelectedPropertyIdUseCase = GetSelectedPropertyIdUseCase(propertyRepository)
    }

    @Test
    fun `invoke returns existing selected property id`() = runTest {
        val selectedIdFlow = MutableStateFlow<Long?>(1234L)
        every { propertyRepository.selectedId } returns selectedIdFlow

        val result = getSelectedPropertyIdUseCase().first()

        assertThat(result).isEqualTo(1234L)
    }

    @Test
    fun `invoke returns null when no property is selected`() = runTest {
        val selectedIdFlow = MutableStateFlow<Long?>(null)
        every { propertyRepository.selectedId } returns selectedIdFlow

        val result = getSelectedPropertyIdUseCase().first()

        assertThat(result).isNull()
    }
}
