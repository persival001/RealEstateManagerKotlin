package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.photo.DeletePhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.GetPropertyPhotosUseCase
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.InsertPointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.UpdatePointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.InsertPropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property.UpdatePropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.user.GetRealEstateAgentUseCase
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class AddOrModifyPropertyViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mocks
    private val insertPhotoUseCase: InsertPhotoUseCase = mockk()
    private val insertPropertyUseCase: InsertPropertyUseCase = mockk()
    private val insertPointOfInterestUseCase: InsertPointOfInterestUseCase = mockk()
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase = mockk()
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase = mockk()
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase = mockk()
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase = mockk()
    private val getPropertyPhotosUseCase: GetPropertyPhotosUseCase = mockk()
    private val refreshStoragePermissionUseCase: RefreshStoragePermissionUseCase = mockk()
    private val refreshCameraPermissionUseCase: RefreshCameraPermissionUseCase = mockk()
    private val hasCameraPermissionUseCase: HasCameraPermissionUseCase = mockk()
    private val hasStoragePermissionUseCase: HasStoragePermissionUseCase = mockk()
    private val updatePointOfInterestUseCase: UpdatePointOfInterestUseCase = mockk()
    private val updatePropertyUseCase: UpdatePropertyUseCase = mockk()
    private val deletePhotoUseCase: DeletePhotoUseCase = mockk()

    private val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2023)
        set(Calendar.MONTH, Calendar.NOVEMBER)
        set(Calendar.DAY_OF_MONTH, 2)
    }

    private lateinit var viewModel: AddOrModifyPropertyViewModel

    @Before
    fun setUp() {
        val selectedPropertyIdStateFlow = MutableStateFlow<Long?>(1L)
        every { getSelectedPropertyIdUseCase.invoke() } returns selectedPropertyIdStateFlow.asStateFlow()
        every { getPropertyPhotosUseCase.invoke(1) } returns flowOf(listOf())


        viewModel = AddOrModifyPropertyViewModel(
            insertPhotoUseCase = insertPhotoUseCase,
            insertPropertyUseCase = insertPropertyUseCase,
            insertPointOfInterestUseCase = insertPointOfInterestUseCase,
            getRealEstateAgentUseCase = getRealEstateAgentUseCase,
            getPropertyWithPhotoAndPOIUseCase = getPropertyWithPhotoAndPOIUseCase,
            getSelectedPropertyIdUseCase = getSelectedPropertyIdUseCase,
            getSavedStateForDateConversionButtonUseCase = getSavedStateForDateConversionButtonUseCase,
            getPropertyPhotosUseCase = getPropertyPhotosUseCase,
            refreshStoragePermissionUseCase = refreshStoragePermissionUseCase,
            refreshCameraPermissionUseCase = refreshCameraPermissionUseCase,
            hasCameraPermissionUseCase = hasCameraPermissionUseCase,
            hasStoragePermissionUseCase = hasStoragePermissionUseCase,
            updatePointOfInterestUseCase = updatePointOfInterestUseCase,
            updatePropertyUseCase = updatePropertyUseCase,
            deletePhotoUseCase = deletePhotoUseCase,
        )
    }

    @Test
    fun `getFormattedDate should return formatted date in English when French date is not enabled`() =
        testCoroutineRule.runTest {
            // Arrange
            val mockDate = calendar.time
            val expectedFormattedDate = "2023-11-02"
            coEvery { getSavedStateForDateConversionButtonUseCase.invoke() } returns flowOf(false)

            // Act
            val result = viewModel.getFormattedDate(mockDate)

            // Assert
            assertEquals(expectedFormattedDate, result)
        }

    @Test
    fun `getFormattedDate should return formatted date in French when French date is enabled`() =
        testCoroutineRule.runTest {
            // Arrange
            val mockDate = calendar.time
            val expectedFormattedDate = "02-11-2023"
            coEvery { getSavedStateForDateConversionButtonUseCase.invoke() } returns flowOf(true)

            // Act
            val result = viewModel.getFormattedDate(mockDate)

            // Assert
            assertEquals(expectedFormattedDate, result)
        }

    @Test
    fun `getFormattedDate should return error message on exception`() =
        testCoroutineRule.runTest {
            // Arrange
            val mockDate = calendar.time
            coEvery { getSavedStateForDateConversionButtonUseCase.invoke() } throws Exception("Test exception")

            // Act
            val result = viewModel.getFormattedDate(mockDate)

            // Assert
            assertEquals("Format date error", result)
        }

    @Test
    fun `when refreshStoragePermission is called, should refresh storage permission`() {
        // Arrange
        justRun { refreshStoragePermissionUseCase.invoke() }

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
    fun `when hasCameraPermission is called, should return camera permission status`() =
        testCoroutineRule.runTest {
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
    fun `when hasStoragePermission is called, should return storage permission status`() =
        testCoroutineRule.runTest {
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
