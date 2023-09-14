package com.persival.realestatemanagerkotlin.ui.properties

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForCurrencyConversionButton
import com.persival.realestatemanagerkotlin.domain.database.SynchronizeDatabaseUseCase
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.search.GetActiveSearchFilterUseCase
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PropertiesViewModelTest {
    // Mocks
    private val getAllPropertiesWithPhotosAndPOIUseCase = mockk<GetAllPropertiesWithPhotosAndPOIUseCase>()
    private val setSelectedPropertyIdUseCase = mockk<SetSelectedPropertyIdUseCase>(relaxed = true)
    private val synchronizeDatabaseUseCase = mockk<SynchronizeDatabaseUseCase>(relaxed = true)
    private val getSavedStateForCurrencyConversionButton = mockk<GetSavedStateForCurrencyConversionButton>()
    private val getActiveSearchFilterUseCase = mockk<GetActiveSearchFilterUseCase>()

    // System Under Test
    private lateinit var viewModel: PropertiesViewModel

    @Before
    fun setup() {
        clearAllMocks()

        viewModel = PropertiesViewModel(
            getAllPropertiesWithPhotosAndPOIUseCase,
            setSelectedPropertyIdUseCase,
            synchronizeDatabaseUseCase,
            getSavedStateForCurrencyConversionButton,
            getActiveSearchFilterUseCase
        )
    }

    @Test
    fun `when updating selected property ID, should invoke usecase`() {
        // Act
        viewModel.updateSelectedPropertyId(5L)

        // Assert
        verify { setSelectedPropertyIdUseCase.invoke(5L) }
    }

    @Test
    fun `when synchronizeDatabase is called, should invoke usecase`() {
        // Act
        viewModel.synchronizeDatabase()

        // Assert
        verify { synchronizeDatabaseUseCase.invoke() }
    }

}
