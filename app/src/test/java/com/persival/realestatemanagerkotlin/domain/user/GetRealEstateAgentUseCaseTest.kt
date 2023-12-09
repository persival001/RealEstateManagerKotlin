package com.persival.realestatemanagerkotlin.domain.user

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.persival.realestatemanagerkotlin.domain.user.model.RealEstateAgent
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class GetRealEstateAgentUseCaseTest {

    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var getRealEstateAgentUseCase: GetRealEstateAgentUseCase

    @Before
    fun setUp() {
        firebaseRepository = mockk()
        getRealEstateAgentUseCase = GetRealEstateAgentUseCase(firebaseRepository)
    }

    @Test
    fun `invoke returns real estate agent identity when available`() {
        // Given
        val agentIdentity = RealEstateAgent("John Doe", "john@example.com")
        every { firebaseRepository.getRealEstateAgentIdentity() } returns agentIdentity

        // When
        val result = getRealEstateAgentUseCase.invoke()

        // Then
        assertThat(result).isEqualTo(agentIdentity)
    }

    @Test
    fun `invoke returns null when no agent identity is available`() {
        // Given
        every { firebaseRepository.getRealEstateAgentIdentity() } returns null

        // When
        val result = getRealEstateAgentUseCase.invoke()

        // Then
        assertThat(result).isNull()
    }
}
