package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.photo.GetPhotoIdsForPropertyUseCase
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.photo.UpdatePhotoUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.InsertPointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.UpdatePointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.InsertPropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property.UpdatePropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.domain.user.GetRealEstateAgentUseCase
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddOrModifyPropertyViewModel @Inject constructor(
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase,
    private val insertPropertyUseCase: InsertPropertyUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val insertPointOfInterestUseCase: InsertPointOfInterestUseCase,
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase,
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase,
    private val refreshStoragePermissionUseCase: RefreshStoragePermissionUseCase,
    private val refreshCameraPermissionUseCase: RefreshCameraPermissionUseCase,
    private val hasCameraPermissionUseCase: HasCameraPermissionUseCase,
    private val hasStoragePermissionUseCase: HasStoragePermissionUseCase,
    private val getPhotoIdsForPropertyUseCase: GetPhotoIdsForPropertyUseCase,
    private val updatePointOfInterestUseCase: UpdatePointOfInterestUseCase,
    private val updatePropertyUseCase: UpdatePropertyUseCase,
    private val updatePhotoUseCase: UpdatePhotoUseCase,

    ) : ViewModel() {

    val viewStateLiveData: LiveData<AddOrModifyPropertyViewState> = liveData {
        val propertyId = getSelectedPropertyIdUseCase().value
        if (propertyId != null && propertyId > 0) {
            getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { propertyWithPhotosAndPOIEntity ->
                val photoIds = propertyWithPhotosAndPOIEntity.photos.map { it.id }
                Log.d("ViewModel", "Photo IDs: $photoIds")
                emit(mapEntityToViewState(propertyWithPhotosAndPOIEntity))
            }
        }
    }

    fun addNewProperty(addOrModifyPropertyViewState: AddOrModifyPropertyViewState) {
        viewModelScope.launch {
            val agentEntity = getRealEstateAgentUseCase.invoke()

            if (agentEntity != null) {
                val propertyId = insertPropertyUseCase.invoke(
                    PropertyEntity(
                        id = 0,
                        type = addOrModifyPropertyViewState.type,
                        address = addOrModifyPropertyViewState.address,
                        latLng = addOrModifyPropertyViewState.latLng,
                        area = addOrModifyPropertyViewState.area,
                        rooms = addOrModifyPropertyViewState.rooms,
                        bathrooms = addOrModifyPropertyViewState.bathrooms,
                        bedrooms = addOrModifyPropertyViewState.bedrooms,
                        description = addOrModifyPropertyViewState.description,
                        price = addOrModifyPropertyViewState.price,
                        isSold = isThePropertyForSale(addOrModifyPropertyViewState.soldAt),
                        entryDate = addOrModifyPropertyViewState.availableFrom,
                        saleDate = addOrModifyPropertyViewState.soldAt,
                        agentName = agentEntity.name
                    )
                )

                if (propertyId != null) {
                    val photoIds =
                        getSelectedPropertyIdUseCase.invoke().value?.let {
                            getPhotoIdsForPropertyUseCase.invoke(it).first()
                        }

                    addOrModifyPropertyViewState.photoUris.forEachIndexed { index, uri ->
                        val photoId = if (index < (photoIds?.size ?: 0)) photoIds?.get(index) else null
                        insertPhotoUseCase.invoke(
                            PhotoEntity(
                                id = photoId ?: 0L,
                                propertyId = propertyId,
                                description = addOrModifyPropertyViewState.photoDescriptions.getOrNull(index) ?: "",
                                photoUrl = uri
                            )
                        )
                    }

                    addOrModifyPropertyViewState.pointsOfInterest.split(",").forEach {
                        insertPointOfInterestUseCase.invoke(
                            PointOfInterestEntity(propertyId = propertyId, poi = it.trim())
                        )
                    }
                }
            }
        }
    }


    fun updateProperty(addOrModifyPropertyViewState: AddOrModifyPropertyViewState) {
        val propertyId = getSelectedPropertyIdUseCase().value
        val agentName = getRealEstateAgentUseCase.invoke()?.name

        if (agentName != null && propertyId != null) {
            viewModelScope.launch {
                val propertyEntity = mapToPropertyEntity(addOrModifyPropertyViewState, agentName, propertyId)
                val photoEntities = mapToPhotoEntity(addOrModifyPropertyViewState, propertyId)
                val pointOfInterestEntities = mapToPointOfInterestEntity(addOrModifyPropertyViewState, propertyId)

                // Update the property
                updatePropertyUseCase.invoke(propertyEntity)

                // Update the photos
                updatePhotoUseCase.invoke(propertyId, photoEntities)

                // Update the points of interest
                updatePointOfInterestUseCase.invoke(propertyId, pointOfInterestEntities)
            }
        }
    }

    private fun isThePropertyForSale(saleDate: String?): Boolean = !(saleDate == null || saleDate == "")

    private fun mapToPhotoEntity(
        addOrModifyPropertyViewState: AddOrModifyPropertyViewState,
        propertyId: Long
    ): List<PhotoEntity> {
        return addOrModifyPropertyViewState.photoUris.mapIndexed { index, uri ->
            PhotoEntity(
                propertyId = propertyId,
                description = addOrModifyPropertyViewState.photoDescriptions.getOrNull(index) ?: "",
                photoUrl = uri
            )
        }
    }

    private fun mapToPointOfInterestEntity(
        addOrModifyPropertyViewState: AddOrModifyPropertyViewState,
        propertyId: Long
    ): List<PointOfInterestEntity> {
        return addOrModifyPropertyViewState.pointsOfInterest.split(",").map { poi ->
            PointOfInterestEntity(
                propertyId = propertyId,
                poi = poi.trim()
            )
        }
    }

    private fun mapToPropertyEntity(
        addOrModifyPropertyViewState: AddOrModifyPropertyViewState,
        agentName: String,
        propertyId: Long
    ): PropertyEntity {
        return PropertyEntity(
            propertyId,
            addOrModifyPropertyViewState.type,
            addOrModifyPropertyViewState.address,
            addOrModifyPropertyViewState.latLng,
            addOrModifyPropertyViewState.area,
            addOrModifyPropertyViewState.rooms,
            addOrModifyPropertyViewState.bathrooms,
            addOrModifyPropertyViewState.bedrooms,
            addOrModifyPropertyViewState.description,
            addOrModifyPropertyViewState.price,
            isThePropertyForSale(addOrModifyPropertyViewState.soldAt),
            addOrModifyPropertyViewState.availableFrom,
            addOrModifyPropertyViewState.soldAt,
            agentName
        )
    }

    private fun mapEntityToViewState(entity: PropertyWithPhotosAndPOIEntity): AddOrModifyPropertyViewState {
        return AddOrModifyPropertyViewState(
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
