package com.persival.realestatemanagerkotlin.ui.properties

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.persival.realestatemanagerkotlin.domain.conversion.IsEuroConversionEnabledUseCase
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.search.GetActiveSearchFilterUseCase
import com.persival.realestatemanagerkotlin.domain.search.GetSearchedPropertiesUseCase
import com.persival.realestatemanagerkotlin.domain.search.SetSearchedPropertiesUseCase
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PropertiesViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PropertiesViewModel
    private val getAllPropertiesWithPhotosAndPOIUseCase = mockk<GetAllPropertiesWithPhotosAndPOIUseCase>(relaxed = true)
    private val setSelectedPropertyIdUseCase = mockk<SetSelectedPropertyIdUseCase>(relaxed = true)
    private val isEuroConversionEnabledUseCase = mockk<IsEuroConversionEnabledUseCase>(relaxed = true)
    private val getActiveSearchFilterUseCase = mockk<GetActiveSearchFilterUseCase>(relaxed = true)
    private val getSearchedPropertiesUseCase = mockk<GetSearchedPropertiesUseCase>(relaxed = true)
    private val setSearchedPropertiesUseCase = mockk<SetSearchedPropertiesUseCase>(relaxed = true)

    private val propertyObserver = mockk<Observer<List<PropertyViewStateItem>>>(relaxed = true)
    private val selectedIdObserver = mockk<Observer<Long?>>(relaxed = true)

    @Before
    fun setUp() {
        viewModel = PropertiesViewModel(
            getAllPropertiesWithPhotosAndPOIUseCase,
            setSelectedPropertyIdUseCase,
            isEuroConversionEnabledUseCase,
            getActiveSearchFilterUseCase,
            getSearchedPropertiesUseCase,
            setSearchedPropertiesUseCase
        )

        viewModel.properties.observeForever(propertyObserver)
        viewModel.updateSelectedPropertyId(1L)
    }

    /*@Test
    fun `updateSelectedPropertyId updates propertyIdSelected`() = testCoroutineRule.runTest {
        val propertyId = 1L

        viewModel.updateSelectedPropertyId(propertyId)

        verify { setSelectedPropertyIdUseCase.invoke(propertyId) }
        verify { selectedIdObserver.onChanged(propertyId) }
    }*/

    @Test
    fun `onResetFilter clears search filter`() = testCoroutineRule.runTest {
        viewModel.onResetFilter()

        verify { setSearchedPropertiesUseCase.invoke(null) }
    }

}
