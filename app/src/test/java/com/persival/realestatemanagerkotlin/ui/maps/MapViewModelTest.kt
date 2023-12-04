package com.persival.realestatemanagerkotlin.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.persival.realestatemanagerkotlin.domain.location.GetLocationUseCase
import com.persival.realestatemanagerkotlin.domain.location.model.LocationEntity
import com.persival.realestatemanagerkotlin.domain.permissions.IsGpsActivatedUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshGpsActivationUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshLocationPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule

class MapViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val getAllPropertiesWithPhotosAndPOIUseCase =
        mockk<GetAllPropertiesWithPhotosAndPOIUseCase>()
    private val getLocationUseCase = mockk<GetLocationUseCase>()
    private val setSelectedPropertyIdUseCase = mockk<SetSelectedPropertyIdUseCase>()
    private val refreshGpsActivationUseCase = mockk<RefreshGpsActivationUseCase>()
    private val refreshLocationPermissionUseCase = mockk<RefreshLocationPermissionUseCase>()
    private val isGpsActivatedUseCase = mockk<IsGpsActivatedUseCase>()
    private val getSelectedPropertyIdUseCase = mockk<GetSelectedPropertyIdUseCase>()

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = MapViewModel(
            getAllPropertiesWithPhotosAndPOIUseCase,
            getLocationUseCase,
            setSelectedPropertyIdUseCase,
            refreshGpsActivationUseCase,
            refreshLocationPermissionUseCase,
            isGpsActivatedUseCase,
            getSelectedPropertyIdUseCase
        )

        coEvery { getLocationUseCase.invoke() } returns flowOf(LocationEntity(125.45, 54.145))
        coEvery { getSelectedPropertyIdUseCase.invoke() } returns MutableStateFlow(1L)
    }


}

