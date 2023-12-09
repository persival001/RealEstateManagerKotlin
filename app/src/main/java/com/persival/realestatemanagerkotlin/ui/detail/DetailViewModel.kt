package com.persival.realestatemanagerkotlin.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase,
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
) : ViewModel() {

    val detailViewStateLiveData: LiveData<DetailViewState> = liveData {
        val propertyId = getSelectedPropertyIdUseCase.invoke().firstOrNull()
        if (propertyId != null) {
            getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { property ->
                emit(
                    DetailViewState(
                        propertyId = propertyId,
                        type = property.property.type,
                        price = property.property.price.toString(),
                        surface = property.property.area.toString(),
                        rooms = property.property.rooms.toString(),
                        bedrooms = property.property.bedrooms.toString(),
                        bathrooms = property.property.bathrooms.toString(),
                        description = property.property.description,
                        address = property.property.address,
                        pointOfInterest = convertPOIToString(property.pointsOfInterest),
                        isSold = property.property.isSold,
                        entryDate = property.property.entryDate,
                        saleDate = property.property.saleDate ?: "",
                        agentName = property.property.agentName,
                        isLatLongAvailable = property.property.latLng.isNotEmpty(),
                        pictures = mapPhotosToViewStateItems(property.photos)
                    )
                )
            }
        }
    }

    private fun convertPOIToString(pointsOfInterest: List<PointOfInterest>): String =
        pointsOfInterest.joinToString(", ") { it.poi }

    private fun mapPhotosToViewStateItems(photos: List<Photo>): List<DetailPhotoViewStateItem> {
        return photos.map { photo ->
            DetailPhotoViewStateItem(url = photo.photoUrl, caption = photo.description)
        }
    }

}