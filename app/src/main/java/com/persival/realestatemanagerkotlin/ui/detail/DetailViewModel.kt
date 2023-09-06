package com.persival.realestatemanagerkotlin.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase,
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
) : ViewModel() {
    private val selectedIdLiveData = MutableLiveData<Long?>()
    val selectedId: LiveData<Long?> get() = selectedIdLiveData

    private val detailLiveData = MutableLiveData<DetailViewState>()
    val details: LiveData<DetailViewState> = detailLiveData

    private val detailItemLiveData = MutableLiveData<DetailViewStateItem>()
    val detailItem: LiveData<DetailViewStateItem> = detailItemLiveData

    fun fetchAndLoadDetailsForSelectedProperty() {
        val id = getSelectedPropertyIdUseCase()
        selectedIdLiveData.value = id

        id?.let {
            loadItemDetails(it)
            loadDetails(it)
        }
    }

    private fun loadDetails(propertyId: Long) {
        viewModelScope.launch {
            getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { details ->
                val viewState = DetailViewState(
                    propertyId = details.property.id,
                    type = details.property.type,
                    price = details.property.price.toString(),
                    surface = details.property.area.toString(),
                    rooms = details.property.rooms.toString(),
                    bedrooms = details.property.bedrooms.toString(),
                    bathrooms = details.property.bathrooms.toString(),
                    description = details.property.description,
                    address = details.property.address,
                    pointOfInterest = convertPOIToString(details.pointsOfInterest),
                    isSold = details.property.isSold,
                    entryDate = details.property.entryDate,
                    saleDate = details.property.saleDate ?: "",
                    agentName = details.property.agentName
                )

                detailLiveData.value = viewState
            }
        }
    }

    private fun loadItemDetails(propertyId: Long) {
        viewModelScope.launch {
            getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { details ->
                val urls = details.photos.map { it.photoUrl }
                val captions = details.photos.map { it.description }

                val detailViewState = DetailViewStateItem(
                    url = urls,
                    caption = captions
                )

                detailItemLiveData.value = detailViewState
            }
        }
    }

    private fun convertPOIToString(pointsOfInterest: List<PointOfInterestEntity>): String =
        pointsOfInterest.joinToString(", ") { it.poi }

    fun setSelectedPropertyId(id: Long) {
        selectedIdLiveData.value = id
        fetchAndLoadDetailsForSelectedProperty()
    }


}