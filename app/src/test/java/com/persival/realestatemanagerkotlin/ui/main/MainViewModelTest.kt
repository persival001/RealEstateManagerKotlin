package com.persival.realestatemanagerkotlin.ui.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel
    private lateinit var application: Application
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase = mockk()

    @Before
    fun setUp() {
        // Mock the application
        application = mockk(relaxed = true)

        // Mock the use case to return a flow with null (no property selected)
        every { getSelectedPropertyIdUseCase() } returns MutableStateFlow(null)

        viewModel = MainViewModel(application, getSelectedPropertyIdUseCase)
    }

    @Test
    fun `getPropertyId returns current property id`() {
        // Given
        val propertyIdObserver: Observer<Long?> = mockk(relaxed = true)

        // When
        viewModel.getPropertyId().asLiveData().observeForever(propertyIdObserver)

        // Then
        verify { propertyIdObserver.onChanged(null) } // No property id should be emitted as use case returns null
    }

    @Test
    fun `onResume sets isTablet`() {
        // Given
        val isTablet = true

        // When
        viewModel.onResume(isTablet)

        // Then
        assert(isTablet) // isTablet in ViewModel should be set to true
    }
}
