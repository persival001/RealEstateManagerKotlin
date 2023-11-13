package com.persival.realestatemanagerkotlin.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun `fetchAndLoadDetailsForSelectedProperty updates detailLiveData`() = runBlockingTest {
        // Given
        val propertyId = 1L
        //val detail = createFakeDetail() // You would create a fake DetailViewState here
        val items = listOf(DetailViewStateItem(url = "fake_url", caption = "fake_caption"))

        coEvery { getSelectedPropertyIdUseCase.invoke().value } returns propertyId
        //coEvery { getPropertyWithPhotoAndPOIUseCase.invoke(propertyId) } returns flowOf(detail)

        // When
        viewModel.fetchAndLoadDetailsForSelectedProperty()

        // Then
        verify { detailViewStateObserver.onChanged(any()) }
        verify { detailViewStateItemObserver.onChanged(items) }
        verify { selectedIdObserver.onChanged(propertyId) }
    }

    @Test
    fun `fetchAndLoadDetailsForSelectedProperty with null selectedId doesn't update details`() = runBlockingTest {
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
    fun `fetchAndLoadDetailsForSelectedProperty with exception emits error state`() = runBlockingTest {
        // Given
        val propertyId = 1L
        val exception = RuntimeException("Something went wrong")

        coEvery { getSelectedPropertyIdUseCase.invoke().value } returns propertyId
        coEvery { getPropertyWithPhotoAndPOIUseCase.invoke(propertyId) } returns flow { throw exception }

        // When
        viewModel.fetchAndLoadDetailsForSelectedProperty()

        // Then
        // You should add an error LiveData observer and check if it gets updated with the correct error.
    }

    @Test
    fun `fetchAndLoadDetailsForSelectedProperty updates live data on successful data fetch`() = runBlockingTest {
        // Mock the responses for use cases
        val propertyId = 1L
        val propertyWithPhotosAndPOIEntity: PropertyWithPhotosAndPOIEntity = mockk()

        coEvery { getSelectedPropertyIdUseCase.invoke().value } returns propertyId
        coEvery { getPropertyWithPhotoAndPOIUseCase.invoke(propertyId) } returns flowOf(propertyWithPhotosAndPOIEntity)

        // Mocking the observers
        val selectedIdObserver: Observer<Long?> = mockk(relaxed = true)
        val detailViewStateObserver: Observer<DetailViewState> = mockk(relaxed = true)
        val detailViewStateItemObserver: Observer<List<DetailViewStateItem>> = mockk(relaxed = true)

        viewModel.selectedId.observeForever(selectedIdObserver)
        viewModel.details.observeForever(detailViewStateObserver)
        viewModel.detailItem.observeForever(detailViewStateItemObserver)

        // Call the method to test
        viewModel.fetchAndLoadDetailsForSelectedProperty()

        // Assertions for the observed values after method call
        verify { selectedIdObserver.onChanged(propertyId) }
        verify { detailViewStateObserver.onChanged(any()) }
        verify { detailViewStateItemObserver.onChanged(any()) }

        // Clean up
        viewModel.selectedId.removeObserver(selectedIdObserver)
        viewModel.details.removeObserver(detailViewStateObserver)
        viewModel.detailItem.removeObserver(detailViewStateItemObserver)
    }


}
