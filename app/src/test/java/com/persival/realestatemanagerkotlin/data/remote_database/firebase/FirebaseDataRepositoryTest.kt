package com.persival.realestatemanagerkotlin.data.remote_database.firebase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class FirebaseDataRepositoryTest {

    private val mockFirebaseAuth: FirebaseAuth = mockk()
    private val mockFirebaseUser: FirebaseUser = mockk()

    private lateinit var firebaseDataRepository: FirebaseDataRepository

    @Before
    fun setUp() {
        firebaseDataRepository = FirebaseDataRepository(mockFirebaseAuth)
    }

    @Test
    fun `getCurrentUser returns current user when logged in`() {
        // Arrange
        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser

        // Act
        val result = firebaseDataRepository.getCurrentUser()

        // Assert
        assert(result == mockFirebaseUser)
    }

    @Test
    fun `getCurrentUser returns null when no user is logged in`() {
        // Arrange
        every { mockFirebaseAuth.currentUser } returns null

        // Act
        val result = firebaseDataRepository.getCurrentUser()

        // Assert
        assert(result == null)
    }

    @Test
    fun `getRealEstateAgentIdentity returns RealEstateAgentEntity when user has displayName`() {
        // Arrange
        val expectedId = "test_user_id"
        val expectedName = "John Doe"
        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns expectedId
        every { mockFirebaseUser.displayName } returns expectedName

        // Act
        val result = firebaseDataRepository.getRealEstateAgentIdentity()

        // Assert
        assertThat(result.id).isEqualTo(expectedId)
        assertThat(result.name).isEqualTo(expectedName)
    }

    @Test
    fun `getRealEstateAgentIdentity throws IllegalStateException when no user is logged in`() {
        // Arrange
        every { mockFirebaseAuth.currentUser } returns null

        // Act & Assert
        assertThrows(IllegalStateException::class.java) { firebaseDataRepository.getRealEstateAgentIdentity() }
    }

    @Test
    fun `getRealEstateAgentIdentity throws IllegalStateException when user has no displayName`() {
        // Arrange
        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
        every { mockFirebaseUser.displayName } returns null

        // Act & Assert
        assertThrows(IllegalStateException::class.java) {
            firebaseDataRepository.getRealEstateAgentIdentity()
        }
    }
}
