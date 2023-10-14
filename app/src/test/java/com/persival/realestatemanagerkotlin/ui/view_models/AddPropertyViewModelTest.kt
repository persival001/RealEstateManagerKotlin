package com.persival.realestatemanagerkotlin.ui.view_models

import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.InsertPointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.InsertPropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.user.GetRealEstateAgentUseCase
import com.persival.realestatemanagerkotlin.ui.add_property.AddPropertyViewModel
import com.persival.realestatemanagerkotlin.utils.Utils
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date

class AddPropertyViewModelTest {

    // Mocks
    private val getRealEstateAgentUseCase = mockk<GetRealEstateAgentUseCase>()
    private val insertPropertyUseCase = mockk<InsertPropertyUseCase>()
    private val insertPhotoUseCase = mockk<InsertPhotoUseCase>()
    private val insertPointOfInterestUseCase = mockk<InsertPointOfInterestUseCase>()
    private val getPropertyWithPhotoAndPOIUseCase = mockk<GetPropertyWithPhotoAndPOIUseCase>()
    private val getSelectedPropertyIdUseCase = mockk<GetSelectedPropertyIdUseCase>()
    private val getSavedStateForDateConversionButtonUseCase = mockk<GetSavedStateForDateConversionButtonUseCase>()
    private val refreshStoragePermissionUseCase = mockk<RefreshStoragePermissionUseCase>()
    private val refreshCameraPermissionUseCase = mockk<RefreshCameraPermissionUseCase>()
    private val hasCameraPermissionUseCase = mockk<HasCameraPermissionUseCase>()
    private val hasStoragePermissionUseCase = mockk<HasStoragePermissionUseCase>()

    private lateinit var viewModel: AddPropertyViewModel

    @Before
    fun setUp() {
        clearAllMocks()
        viewModel = AddPropertyViewModel(
            getRealEstateAgentUseCase,
            insertPropertyUseCase,
            insertPhotoUseCase,
            insertPointOfInterestUseCase,
            getSavedStateForDateConversionButtonUseCase,
            refreshStoragePermissionUseCase,
            refreshCameraPermissionUseCase,
            hasCameraPermissionUseCase,
            hasStoragePermissionUseCase
        )
    }

    @Test
    fun `given a date, when getFormattedDate is called, should return appropriate formatted date`() {
        // Arrange
        val dummyDate = Date()
        val isFrenchDateEnabled = true
        every { getSavedStateForDateConversionButtonUseCase.invoke() } returns isFrenchDateEnabled

        // Act
        val result = viewModel.getFormattedDate(dummyDate)

        // Assert
        assertEquals(Utils.getTodayDateInFrench(dummyDate), result)
    }

    @Test
    fun `when refreshStoragePermission is called, should refresh storage permission`() {
        // Arrange
        coEvery { refreshStoragePermissionUseCase.invoke() } just Runs

        // Act
        viewModel.refreshStoragePermission()

        // Assert
        coVerify { refreshStoragePermissionUseCase.invoke() }
    }

    @Test
    fun `when refreshCameraPermission is called, should refresh camera permission`() {
        // Arrange
        coEvery { refreshCameraPermissionUseCase.invoke() } just Runs

        // Act
        viewModel.refreshCameraPermission()

        // Assert
        coVerify { refreshCameraPermissionUseCase.invoke() }
    }

    @Test
    fun `when hasCameraPermission is called, should return camera permission status`() = runBlockingTest {
        // Arrange
        val hasPermission = true
        coEvery { hasCameraPermissionUseCase.invoke() } returns flowOf(hasPermission)

        // Act
        val result = viewModel.hasCameraPermission().first()

        // Assert
        coVerify { hasCameraPermissionUseCase.invoke() }
        assertEquals(hasPermission, result)
    }

    @Test
    fun `when hasStoragePermission is called, should return storage permission status`() = runBlockingTest {
        // Arrange
        val hasPermission = true
        coEvery { hasStoragePermissionUseCase.invoke() } returns flowOf(hasPermission)

        // Act
        val result = viewModel.hasStoragePermission().first()

        // Assert
        coVerify { hasStoragePermissionUseCase.invoke() }
        assertEquals(hasPermission, result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}
