package com.persival.realestatemanagerkotlin.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase
) : ViewModel() {
    private val detailLiveData = MutableLiveData<DetailViewState>()
    val details: LiveData<DetailViewState> = detailLiveData
    private val detailItemLiveData = MutableLiveData<DetailViewStateItem>()
    val detailItem: LiveData<DetailViewStateItem> = detailItemLiveData

    fun setPropertyId(propertyId: Long) {
        loadItemDetails(propertyId)
        loadDetails(propertyId)
    }

    private fun loadDetails(propertyId: Long) {
        viewModelScope.launch {
            getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { details ->
                val viewState = DetailViewState(
                    propertyId = details.property.id!!,
                    type = details.property.type,
                    price = details.property.price.toString(),
                    surface = details.property.area.toString(),
                    rooms = details.property.rooms.toString(),
                    bedrooms = details.property.bedrooms.toString(),
                    bathrooms = details.property.bathrooms.toString(),
                    description = details.property.description,
                    address = details.property.address,
                    pointOfInterest = "School", //TODO: get from POI
                    isSold = details.property.isSold.toString(),
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
                val detailViewState = DetailViewStateItem(
                    url = details.photos.firstOrNull()?.photoUrl ?: "",
                    caption = details.photos.firstOrNull()?.description ?: "",
                )

                detailItemLiveData.value = detailViewState
            }
        }
    }
}