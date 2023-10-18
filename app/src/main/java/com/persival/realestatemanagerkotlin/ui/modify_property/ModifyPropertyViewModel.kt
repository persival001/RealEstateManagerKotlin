package com.persival.realestatemanagerkotlin.ui.modify_property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.photo.DeletePhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.GetPropertyPhotosUseCase
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.UpdatePointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.property.GetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property.UpdatePropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetPropertyWithPhotoAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.domain.user.GetRealEstateAgentUseCase
import com.persival.realestatemanagerkotlin.utils.EquatableCallback
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ModifyPropertyViewModel @Inject constructor(
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase,
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase,
    private val refreshStoragePermissionUseCase: RefreshStoragePermissionUseCase,
    private val refreshCameraPermissionUseCase: RefreshCameraPermissionUseCase,
    private val hasCameraPermissionUseCase: HasCameraPermissionUseCase,
    private val hasStoragePermissionUseCase: HasStoragePermissionUseCase,
    private val updatePointOfInterestUseCase: UpdatePointOfInterestUseCase,
    private val updatePropertyUseCase: UpdatePropertyUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val getPropertyPhotosUseCase: GetPropertyPhotosUseCase,

    ) : ViewModel() {

    private val viewStateItemListFlow = MutableStateFlow<List<ModifyPropertyViewStateItem>>(emptyList())
    val viewStateItemList: StateFlow<List<ModifyPropertyViewStateItem>> = viewStateItemListFlow

    init {
        viewModelScope.launch {
            val propertyId = getSelectedPropertyIdUseCase().value
            if (propertyId != null && propertyId > 0) {
                getPropertyPhotosUseCase.invoke(propertyId).collect { photoEntityList ->
                    val viewStateItems = photoEntityList.map { mapPhotoEntityToViewStateItem(it) }
                    viewStateItemListFlow.value = viewStateItems
                }
            }
        }
    }

    val viewStateFlow: Flow<ModifyPropertyViewState> = flow {
        val propertyId = getSelectedPropertyIdUseCase().value
        if (propertyId != null && propertyId > 0) {
            getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { propertyWithPhotosAndPOIEntity ->
                emit(mapEntityToViewState(propertyWithPhotosAndPOIEntity))
            }
        }
    }

    fun updateProperty(modifyPropertyViewState: ModifyPropertyViewState) {
        val propertyId = getSelectedPropertyIdUseCase().value
        val agentName = getRealEstateAgentUseCase.invoke()?.name
        if (agentName != null && propertyId != null) {
            viewModelScope.launch {
                val propertyEntity = mapToPropertyEntity(modifyPropertyViewState, agentName, propertyId)
                val pointOfInterestEntities = mapToPointOfInterestEntity(modifyPropertyViewState, propertyId)

                // Update the property
                updatePropertyUseCase.invoke(propertyEntity)

                // Update the points of interests
                updatePointOfInterestUseCase.invoke(propertyId, pointOfInterestEntities)
            }
        }
    }

    fun insertImageAndDescription(uri: String, description: String) {
        viewModelScope.launch {
            getSelectedPropertyIdUseCase().value?.let {
                PhotoEntity(
                    id = 0,
                    propertyId = it,
                    description = description,
                    photoUrl = uri
                )
            }?.let {
                insertPhotoUseCase.invoke(
                    it
                )
            }
        }
    }

    private fun isThePropertyForSale(saleDate: String?): Boolean = !(saleDate == null || saleDate == "")

    private fun mapToPointOfInterestEntity(
        modifyPropertyViewState: ModifyPropertyViewState,
        propertyId: Long
    ): List<PointOfInterestEntity> {
        return modifyPropertyViewState.pointsOfInterest.split(",").map { poi ->
            PointOfInterestEntity(
                propertyId = propertyId,
                poi = poi.trim()
            )
        }
    }

    private fun mapToPropertyEntity(
        modifyPropertyViewState: ModifyPropertyViewState,
        agentName: String,
        propertyId: Long
    ): PropertyEntity {
        return PropertyEntity(
            propertyId,
            modifyPropertyViewState.type,
            modifyPropertyViewState.address,
            modifyPropertyViewState.latLng,
            modifyPropertyViewState.area,
            modifyPropertyViewState.rooms,
            modifyPropertyViewState.bathrooms,
            modifyPropertyViewState.bedrooms,
            modifyPropertyViewState.description,
            modifyPropertyViewState.price,
            isThePropertyForSale(modifyPropertyViewState.soldAt),
            modifyPropertyViewState.availableFrom,
            modifyPropertyViewState.soldAt,
            agentName
        )
    }

    private fun mapEntityToViewState(entity: PropertyWithPhotosAndPOIEntity): ModifyPropertyViewState {
        return ModifyPropertyViewState(
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
            pointsOfInterest = entity.pointsOfInterest.joinToString(",") { it.poi },
        )
    }

    private fun mapPhotoEntityToViewStateItem(entity: PhotoEntity): ModifyPropertyViewStateItem.Photo {
        return ModifyPropertyViewStateItem.Photo(
            id = entity.id,
            propertyId = entity.propertyId,
            description = entity.description,
            photoUrl = entity.photoUrl,
            onDeleteEvent = EquatableCallback {
                viewModelScope.launch {
                    deletePhotoUseCase.invoke(entity.propertyId, entity.id)
                }
            }
        )
    }

    suspend fun getFormattedDate(date: Date): String {
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
