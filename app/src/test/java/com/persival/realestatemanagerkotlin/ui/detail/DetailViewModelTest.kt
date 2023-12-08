package com.persival.realestatemanagerkotlin.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import com.persival.realestatemanagerkotlin.utils_for_tests.observeForTesting
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mock dependencies
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase = mockk()
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase = mockk()

    // ViewModel instance
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        viewModel = DetailViewModel(getPropertyWithPhotoAndPOIUseCase, getSelectedPropertyIdUseCase)
    }

    @Test
    fun `detailViewStateLiveData emits correct DetailViewState`() = testCoroutineRule.runTest {
        // Arrange
        val propertyId = 1L
        val propertyIdStateFlow = MutableStateFlow<Long?>(propertyId)
        val mockProperty = PropertyWithPhotosAndPOIEntity(
            property = PropertyEntity(
                id = propertyId,
                type = "Maison",
                address = "123 rue de Exemple",
                latLng = "45.5017,-73.5673",
                area = 120,
                rooms = 5,
                bathrooms = 2,
                bedrooms = 3,
                description = "Belle maison en centre-ville",
                price = 250000,
                isSold = false,
                entryDate = "-1 month",
                saleDate = null,
                agentName = "John Doe"
            ),
            photos = listOf(
                PhotoEntity(
                    id = 0L,
                    propertyId = propertyId,
                    description = "Façade de la maison",
                    photoUrl = "http://exemple.com/photo.jpg"
                )
            ),
            pointsOfInterest = listOf(
                PointOfInterestEntity(
                    id = 0L,
                    propertyId = propertyId,
                    poi = "Parc à proximité"
                )
            )
        )

        // Assert
        val expectedDetailViewState = DetailViewState(
            propertyId = propertyId,
            type = mockProperty.property.type,
            price = mockProperty.property.price.toString(),
            surface = mockProperty.property.area.toString(),
            rooms = mockProperty.property.rooms.toString(),
            bedrooms = mockProperty.property.bedrooms.toString(),
            bathrooms = mockProperty.property.bathrooms.toString(),
            description = mockProperty.property.description,
            address = mockProperty.property.address,
            pointOfInterest = mockProperty.pointsOfInterest.joinToString(", ") { it.poi },
            isSold = mockProperty.property.isSold,
            entryDate = mockProperty.property.entryDate,
            saleDate = mockProperty.property.saleDate ?: "",
            agentName = mockProperty.property.agentName,
            isLatLongAvailable = mockProperty.property.latLng.isNotEmpty(),
            pictures = mockProperty.photos.map { photo ->
                DetailPhotoViewStateItem(url = photo.photoUrl, caption = photo.description)
            }
        )

        //act
        coEvery { getSelectedPropertyIdUseCase.invoke() } returns propertyIdStateFlow
        coEvery { getPropertyWithPhotoAndPOIUseCase.invoke(propertyId) } returns flowOf(mockProperty)

        viewModel.detailViewStateLiveData.observeForTesting(this) {
            val emittedValue = viewModel.detailViewStateLiveData.value

            assertEquals(expectedDetailViewState, emittedValue)
        }

    }

}
