package com.persival.realestatemanagerkotlin.ui.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
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
import com.persival.realestatemanagerkotlin.ui.add.AddPropertyFragment.Companion.ARG_PROPERTY_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase,
    private val insertPropertyUseCase: InsertPropertyUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val insertPointOfInterestUseCase: InsertPointOfInterestUseCase,
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase,
    private val updatePropertyWithPhotoAndPOIUseCase: UpdatePropertyWithPhotoAndPOIUseCase,
) : ViewModel() {

    val viewStateLiveData: LiveData<AddViewState> = liveData {
        Log.d("AddPropertyVM", "Inside viewStateLiveData")

        val propertyId = requireNotNull(savedStateHandle.get<Long>(ARG_PROPERTY_ID))
        Log.d("AddPropertyVM", "Property ID: $propertyId")

        getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { propertyWithPhotosAndPOIEntity ->
            Log.d("AddPropertyVM", "Received propertyWithPhotosAndPOIEntity: $propertyWithPhotosAndPOIEntity")

            mapEntityToViewState(propertyWithPhotosAndPOIEntity)?.let {
                Log.d("AddPropertyVM", "Mapped entity to view state: $it")
                emit(it)
            } ?: Log.d("AddPropertyVM", "Failed to map entity to view state or entity was null")
        }
    }

    fun addNewProperty(addViewState: AddViewState) {
        viewModelScope.launch {
            val propertyEntity = getRealEstateAgentUseCase.invoke()?.let { propertyEntity ->
                PropertyEntity(
                    0,
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

    private fun isThePropertyForSale(saleDate: String?): Boolean = !(saleDate == null || saleDate == "")

    private fun mapEntityToViewState(entity: PropertyWithPhotosAndPOIEntity?): AddViewState? {
        if (entity == null) {
            return null
        }
        return AddViewState(
            type = entity.property.type,
            address = entity.property.address,
            latLng = entity.property.latLng,
            area = entity.property.area,
            rooms = entity.property.rooms,
            bathrooms = entity.property.bathrooms,
            bedrooms = entity.property.bedrooms,
            description = entity.property.description,
            price = entity.property.price,
            availableFrom = entity.property.entryDate,
            soldAt = entity.property.saleDate ?: "",
            photoUris = entity.photos.map { it.photoUrl },
            photoDescriptions = entity.photos.map { it.description },
            pointsOfInterest = entity.pointsOfInterest.joinToString(",") { it.poi }
        )
    }

}
