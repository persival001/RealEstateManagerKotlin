package com.persival.realestatemanagerkotlin.ui.add_property

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.InsertPointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.InsertPropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.user.GetRealEstateAgentUseCase
import com.persival.realestatemanagerkotlin.utils.EquatableCallback
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase,
    private val insertPropertyUseCase: InsertPropertyUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val insertPointOfInterestUseCase: InsertPointOfInterestUseCase,
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase,
    private val refreshStoragePermissionUseCase: RefreshStoragePermissionUseCase,
    private val refreshCameraPermissionUseCase: RefreshCameraPermissionUseCase,
    private val hasCameraPermissionUseCase: HasCameraPermissionUseCase,
    private val hasStoragePermissionUseCase: HasStoragePermissionUseCase,

    ) : ViewModel() {

    private val addViewStateItemListFlow = MutableStateFlow<List<AddPropertyViewStateItem>>(emptyList())
    val addViewStateItemList: StateFlow<List<AddPropertyViewStateItem>> = addViewStateItemListFlow

    fun addNewProperty(
        addPropertyViewState: AddPropertyViewState,
    ) {
        viewModelScope.launch {
            val agentEntity = getRealEstateAgentUseCase.invoke()
            Log.d("ViewModelDebug", "ADD_PROPERTY_CALLED")

            if (agentEntity != null) {
                val propertyId = insertPropertyUseCase.invoke(
                    PropertyEntity(
                        id = 0,
                        type = addPropertyViewState.type,
                        address = addPropertyViewState.address,
                        latLng = addPropertyViewState.latLng,
                        area = addPropertyViewState.area,
                        rooms = addPropertyViewState.rooms,
                        bathrooms = addPropertyViewState.bathrooms,
                        bedrooms = addPropertyViewState.bedrooms,
                        description = addPropertyViewState.description,
                        price = addPropertyViewState.price,
                        isSold = isThePropertyForSale(addPropertyViewState.soldAt),
                        entryDate = addPropertyViewState.availableFrom,
                        saleDate = addPropertyViewState.soldAt,
                        agentName = agentEntity.name
                    )
                )

                if (propertyId != null) {
                    // Add Image and description

                    // Add point of interests
                    addPropertyViewState.pointsOfInterest.split(",").forEach {
                        insertPointOfInterestUseCase.invoke(
                            PointOfInterestEntity(propertyId = propertyId, poi = it.trim())
                        )
                    }
                }
            }
        }
    }

    fun addImageAndDescription(uri: String, description: String) {
        viewModelScope.launch {
            val newAddPropertyViewStateItem = AddPropertyViewStateItem.Photo(
                id = 0,
                propertyId = 0,
                description = description,
                photoUrl = uri,
                onDeleteEvent = EquatableCallback {
                    viewModelScope.launch {
                        // TODO: remove
                    }
                }
            )

            val currentList = addViewStateItemListFlow.value
            val newList = currentList.toMutableList().apply { add(newAddPropertyViewStateItem) }

            addViewStateItemListFlow.emit(newList)
        }
    }

    private fun isThePropertyForSale(saleDate: String?): Boolean = !(saleDate == null || saleDate == "")

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
