package com.persival.realestatemanagerkotlin.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.InsertPointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.InsertPropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.UpdatePropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.user.GetRealEstateAgentUseCase
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase,
    private val insertPropertyUseCase: InsertPropertyUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val insertPointOfInterestUseCase: InsertPointOfInterestUseCase,
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase,
    private val updatePropertyWithPhotoAndPOIUseCase: UpdatePropertyWithPhotoAndPOIUseCase,
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase,
    private val refreshStoragePermissionUseCase: RefreshStoragePermissionUseCase,
    private val refreshCameraPermissionUseCase: RefreshCameraPermissionUseCase,
    private val hasCameraPermissionUseCase: HasCameraPermissionUseCase,
    private val hasStoragePermissionUseCase: HasStoragePermissionUseCase,
) : ViewModel() {

    val viewStateLiveData: LiveData<AddViewState> = liveData {
        val propertyId = getSelectedPropertyIdUseCase().value
        if (propertyId != null && propertyId > 0) {
            getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { propertyWithPhotosAndPOIEntity ->
                emit(mapEntityToViewState(propertyWithPhotosAndPOIEntity))
            }
        }
    }

    fun addNewProperty(addViewState: AddViewState) {
        viewModelScope.launch {
            val agentEntity = getRealEstateAgentUseCase.invoke()

            if (agentEntity != null) {
                val propertyId = insertPropertyUseCase.invoke(
                    PropertyEntity(
                        id = 0,
                        type = addViewState.type,
                        address = addViewState.address,
                        latLng = addViewState.latLng,
                        area = addViewState.area,
                        rooms = addViewState.rooms,
                        bathrooms = addViewState.bathrooms,
                        bedrooms = addViewState.bedrooms,
                        description = addViewState.description,
                        price = addViewState.price,
                        isSold = isThePropertyForSale(addViewState.soldAt),
                        entryDate = addViewState.availableFrom,
                        saleDate = addViewState.soldAt,
                        agentName = agentEntity.name
                    )
                )

                if (propertyId != null) {
                    addViewState.photoUris.forEachIndexed { index, uri ->
                        insertPhotoUseCase.invoke(
                            PhotoEntity(
                                propertyId = propertyId,
                                description = addViewState.photoDescriptions.getOrNull(index) ?: "",
                                photoUrl = uri
                            )
                        )
                    }

                    addViewState.pointsOfInterest.split(",").forEach {
                        insertPointOfInterestUseCase.invoke(
                            PointOfInterestEntity(
                                propertyId = propertyId,
                                poi = it.trim()
                            )
                        )
                    }
                }
            }
        }
    }

    fun updateProperty(addViewState: AddViewState) {
        val propertyId = getSelectedPropertyIdUseCase().value
        val agentName = getRealEstateAgentUseCase.invoke()?.name

        if (agentName != null && propertyId != null) {
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
                    agentName
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
    }

    private fun isThePropertyForSale(saleDate: String?): Boolean = !(saleDate == null || saleDate == "")

    private fun mapEntityToViewState(entity: PropertyWithPhotosAndPOIEntity): AddViewState {
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
            pointsOfInterest = entity.pointsOfInterest.joinToString(",") { it.poi },
        )
    }

    fun getFormattedDate(date: Date): String {
        val isFrenchDateEnabled = getSavedStateForDateConversionButtonUseCase.invoke()
        return if (isFrenchDateEnabled) {
            Utils.getTodayDateInFrench(date)
        } else {
            Utils.getTodayDate(date)
        }
    }

    fun refreshStoragePermission() {
        refreshStoragePermissionUseCase.invoke()
    }

    fun refreshCameraPermission() {
        refreshCameraPermissionUseCase.invoke()
    }

    fun hasCameraPermission(): Flow<Boolean> {
        return hasCameraPermissionUseCase.invoke()
    }

    fun hasStoragePermission(): Flow<Boolean> {
        return hasStoragePermissionUseCase.invoke()
    }

}
