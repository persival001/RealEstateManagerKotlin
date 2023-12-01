package com.persival.realestatemanagerkotlin.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val getPropertyWithPhotoAndPOIUseCase = mockk<GetPropertyWithPhotoAndPOIUseCase>()
    private val getSelectedPropertyIdUseCase = mockk<GetSelectedPropertyIdUseCase>()

    private lateinit var viewModel: DetailViewModel

    private val detailViewStateObserver = mockk<Observer<DetailViewState>>(relaxUnitFun = true)
    private val detailViewStateItemObserver =
        mockk<Observer<List<DetailViewStateItem>>>(relaxUnitFun = true)
    private val selectedIdObserver = mockk<Observer<Long?>>(relaxUnitFun = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = DetailViewModel(getPropertyWithPhotoAndPOIUseCase, getSelectedPropertyIdUseCase)
        viewModel.details.observeForever(detailViewStateObserver)
        viewModel.detailItem.observeForever(detailViewStateItemObserver)
        viewModel.selectedId.observeForever(selectedIdObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchAndLoadDetailsForSelectedProperty with null selectedId doesn't update details`() =
        testCoroutineRule.runTest {
            // Given
            coEvery { getSelectedPropertyIdUseCase.invoke().value } returns null

            // When
            viewModel.fetchAndLoadDetailsForSelectedProperty()

            // Then
            verify(exactly = 0) { detailViewStateObserver.onChanged(any()) }
            verify(exactly = 0) { detailViewStateItemObserver.onChanged(any()) }
            verify { selectedIdObserver.onChanged(null) }
        }

    @Test
    fun `fetchAndLoadDetailsForSelectedProperty with valid selectedId updates details`() =
        testCoroutineRule.runTest {
            // Given
            val propertyId = 1L
            val mockPropertyWithPOI = mockk<PropertyWithPhotosAndPOIEntity>(relaxed = true)
            val mockPropertyDetails = flowOf(mockPropertyWithPOI)

            coEvery { getSelectedPropertyIdUseCase.invoke().value } returns propertyId
            coEvery { getPropertyWithPhotoAndPOIUseCase.invoke(propertyId) } returns mockPropertyDetails

            // When
            viewModel.fetchAndLoadDetailsForSelectedProperty()

            // Then
            verify { detailViewStateObserver.onChanged(any()) }
            verify { detailViewStateItemObserver.onChanged(any()) }
            verify { selectedIdObserver.onChanged(propertyId) }
        }
}
