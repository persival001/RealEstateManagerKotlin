package com.persival.realestatemanagerkotlin.domain.user

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class GetCurrentUserUseCaseTest {

    private val firebaseRepository = mockk<FirebaseRepository>()
    private val getCurrentUserUseCase = GetCurrentUserUseCase(firebaseRepository)

    @Test
    fun `invoke returns current firebase user when logged in`() {
        // Arrange
        val mockUser = mockk<FirebaseUser>()
        every { firebaseRepository.getCurrentUser() } returns mockUser

        // Act
        val result = getCurrentUserUseCase.invoke()

        // Assert
        assertThat(result).isEqualTo(mockUser)
    }

    @Test
    fun `invoke returns null when no user is logged in`() {
        // Arrange
        every { firebaseRepository.getCurrentUser() } returns null

        // Act
        val result = getCurrentUserUseCase.invoke()

        // Assert
        assertThat(result).isNull()
    }
}
