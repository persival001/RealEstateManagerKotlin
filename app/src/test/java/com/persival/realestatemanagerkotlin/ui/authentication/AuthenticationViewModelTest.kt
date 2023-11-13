package com.persival.realestatemanagerkotlin.ui.authentication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.google.firebase.auth.FirebaseUser
import com.persival.realestatemanagerkotlin.domain.database.SynchronizeDatabaseUseCase
import com.persival.realestatemanagerkotlin.domain.user.GetCurrentUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class AuthenticationViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val getCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val synchronizeDatabaseUseCase = mockk<SynchronizeDatabaseUseCase>()
    private lateinit var viewModel: AuthenticationViewModel

    private val observer: Observer<FirebaseUser?> = mockk(relaxUnitFun = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = AuthenticationViewModel(getCurrentUserUseCase, synchronizeDatabaseUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCurrentUser returns user when available`() {
        // Given
        val firebaseUser: FirebaseUser = mockk()
        every { getCurrentUserUseCase.invoke() } returns firebaseUser

        // When
        viewModel.getCurrentUser().also { user ->
            // Then
            assertThat(user).isEqualTo(firebaseUser)
        }
    }

    @Test
    fun `getCurrentUser returns null when no user is logged in`() {
        // Given
        every { getCurrentUserUseCase.invoke() } returns null

        // When
        viewModel.getCurrentUser().also { user ->
            // Then
            assertThat(user).isNull()
        }
    }

    @Test
    fun `synchronizeDatabase triggers use case execution`() {
        // Given
        coEvery { synchronizeDatabaseUseCase.invoke() } just runs

        // When
        viewModel.synchronizeDatabase()

        // Then
        coVerify { synchronizeDatabaseUseCase.invoke() }
    }
}
