package com.persival.realestatemanagerkotlin.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.InsertPointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.InsertPropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.UpdatePropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.user.GetRealEstateAgentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase,
    private val insertPropertyUseCase: InsertPropertyUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val insertPointOfInterestUseCase: InsertPointOfInterestUseCase,
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase,
    private val updatePropertyWithPhotoAndPOIUseCase: UpdatePropertyWithPhotoAndPOIUseCase,
) : ViewModel() {

    fun addNewProperty(addViewState: AddViewState) {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            val propertyEntity = getRealEstateAgentUseCase.invoke()?.let { propertyEntity ->
                PropertyEntity(
                    null,
                    addViewState.type,
                    addViewState.address,
                    addViewState.latLng,
                    addViewState.area,
                    addViewState.rooms,
                    addViewState.bathrooms,
                    addViewState.bedrooms,
                    addViewState.description,
                    addViewState.price,
                    isThePropertyForSale(addViewState.soldAt),
                    addViewState.availableFrom,
                    addViewState.soldAt,
                    propertyEntity.name
                )
            }

            val propertyId: Long? = propertyEntity?.let { insertPropertyUseCase.invoke(it) }

            addViewState.photoUris.forEachIndexed { index, uri ->
                val photoEntity = propertyId?.let {
                    PhotoEntity(
                        propertyId = it,
                        description = addViewState.photoDescriptions.getOrNull(index) ?: "",
                        photoUrl = uri
                    )
                }
                if (photoEntity != null) {
                    insertPhotoUseCase.invoke(photoEntity)
                }
            }

            addViewState.pointsOfInterest.split(",").forEach {
                val pointOfInterestEntity = PointOfInterestEntity(
                    propertyId = propertyId ?: 0,
                    poi = it.trim()
                )
                insertPointOfInterestUseCase.invoke(pointOfInterestEntity)
            }
        }
    }

    fun updateProperty(propertyId: Long, addViewState: AddViewState) {
        viewModelScope.launch {
            val propertyEntity = PropertyEntity(
                propertyId,
                addViewState.type,
                addViewState.address,
                addViewState.latLng,
                addViewState.area,
                addViewState.rooms,
                addViewState.bathrooms,
                addViewState.bedrooms,
                addViewState.description,
                addViewState.price,
                isThePropertyForSale(addViewState.soldAt),
                addViewState.availableFrom,
                addViewState.soldAt,
                getRealEstateAgentUseCase.invoke()?.name ?: ""
            )

            val photoEntities = addViewState.photoUris.mapIndexed { index, uri ->
                PhotoEntity(
                    propertyId = propertyId,
                    description = addViewState.photoDescriptions.getOrNull(index) ?: "",
                    photoUrl = uri
                )
            }

            val pointOfInterestEntities = addViewState.pointsOfInterest.split(",").map { poi ->
                PointOfInterestEntity(
                    propertyId = propertyId,
                    poi = poi.trim()
                )
            }

            updatePropertyWithPhotoAndPOIUseCase.invoke(propertyEntity, photoEntities, pointOfInterestEntities)
        }
    }


    fun getProperty(propertyId: Long): LiveData<PropertyWithPhotosAndPOIEntity> {
        return getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).asLiveData()
    }

    private fun isThePropertyForSale(saleDate: String?): Boolean {
        return !(saleDate == null || saleDate == "")
    }
}
