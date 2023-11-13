package com.persival.realestatemanagerkotlin.domain.property

import io.mockk.every
import io.mockk.mockk
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SetSelectedPropertyIdUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var setSelectedPropertyIdUseCase: SetSelectedPropertyIdUseCase

    @Before
    fun setUp() {
        propertyRepository = mockk(relaxed = true)
        setSelectedPropertyIdUseCase = SetSelectedPropertyIdUseCase(propertyRepository)
    }

    @Test
    fun `invoke sets selected property id`() {
        val propertyId = 1234L

        every { propertyRepository.setSelectedId(propertyId) } just runs

        setSelectedPropertyIdUseCase(propertyId)

        verify { propertyRepository.setSelectedId(propertyId) }
    }

    @Test
    fun `invoke clears selected property id when null is provided`() {
        every { propertyRepository.setSelectedId(null) } just runs

        setSelectedPropertyIdUseCase(null)

        verify { propertyRepository.setSelectedId(null) }
    }
}
