package com.persival.realestatemanagerkotlin.ui.maps

import com.persival.realestatemanagerkotlin.domain.location.GetLocationUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.IsGpsActivatedUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshGpsActivationUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshLocationPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {

    // Use the TestCoroutineRule for testing with coroutines
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // Mocks for dependencies
    private val getAllPropertiesWithPhotosAndPOIUseCase = mockk<GetAllPropertiesWithPhotosAndPOIUseCase>()
    private val getLocationUseCase = mockk<GetLocationUseCase>()
    private val setSelectedPropertyIdUseCase = mockk<SetSelectedPropertyIdUseCase>()
    private val refreshGpsActivationUseCase = mockk<RefreshGpsActivationUseCase>()
    private val refreshLocationPermissionUseCase = mockk<RefreshLocationPermissionUseCase>()
    private val isGpsActivatedUseCase = mockk<IsGpsActivatedUseCase>()
    private val getSelectedPropertyIdUseCase = mockk<GetSelectedPropertyIdUseCase>()

    // The ViewModel under test
    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        viewModel = MapViewModel(
            getAllPropertiesWithPhotosAndPOIUseCase,
            getLocationUseCase,
            setSelectedPropertyIdUseCase,
            refreshGpsActivationUseCase,
            refreshLocationPermissionUseCase,
            isGpsActivatedUseCase,
            getSelectedPropertyIdUseCase
        )
    }

    @Test
    fun `isGpsActivated returns correct value`() = testCoroutineRule.runTest {
        // Given
        val gpsActivated = true
        coEvery { isGpsActivatedUseCase.invoke() } returns flowOf(gpsActivated)

        // When
        val result = viewModel.isGpsActivated().first()

        // Then
        assertEquals(gpsActivated, result)
    }

    @Test
    fun `refreshGpsActivation invokes use case`() {
        // When
        viewModel.refreshGpsActivation()

        // Then
        verify { refreshGpsActivationUseCase.invoke() }
    }

    @Test
    fun `refreshLocationPermission invokes use case`() {
        // When
        viewModel.refreshLocationPermission()

        // Then
        verify { refreshLocationPermissionUseCase.invoke() }
    }

    @Test
    fun `updateSelectedPropertyId invokes use case`() {
        // Given
        val propertyId = 1L

        // When
        viewModel.updateSelectedPropertyId(propertyId)

        // Then
        verify { setSelectedPropertyIdUseCase.invoke(propertyId) }
    }

}
