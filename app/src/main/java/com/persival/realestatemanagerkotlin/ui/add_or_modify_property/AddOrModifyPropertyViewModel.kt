package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.persival.realestatemanagerkotlin.BuildConfig.MAPS_API_KEY
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForDateConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.HasStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshCameraPermissionUseCase
import com.persival.realestatemanagerkotlin.domain.permissions.RefreshStoragePermissionUseCase
import com.persival.realestatemanagerkotlin.domain.photo.DeletePhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.GetPropertyPhotosUseCase
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
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
import com.persival.realestatemanagerkotlin.utils.EquatableCallbackWithParam
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddOrModifyPropertyViewModel @Inject constructor(
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val insertPropertyUseCase: InsertPropertyUseCase,
    private val insertPointOfInterestUseCase: InsertPointOfInterestUseCase,
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase,
    private val getPropertyWithPhotoAndPOIUseCase: GetPropertyWithPhotoAndPOIUseCase,
    private val getSelectedPropertyIdUseCase: GetSelectedPropertyIdUseCase,
    private val getSavedStateForDateConversionButtonUseCase: GetSavedStateForDateConversionButtonUseCase,
    private val getPropertyPhotosUseCase: GetPropertyPhotosUseCase,
    private val refreshStoragePermissionUseCase: RefreshStoragePermissionUseCase,
    private val refreshCameraPermissionUseCase: RefreshCameraPermissionUseCase,
    private val hasCameraPermissionUseCase: HasCameraPermissionUseCase,
    private val hasStoragePermissionUseCase: HasStoragePermissionUseCase,
    private val updatePointOfInterestUseCase: UpdatePointOfInterestUseCase,
    private val updatePropertyUseCase: UpdatePropertyUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
) : ViewModel() {

    private val viewStateItemListFlow = MutableStateFlow<List<AddOrModifyPropertyViewStateItem>>(emptyList())
    val viewStateItemList: StateFlow<List<AddOrModifyPropertyViewStateItem>> = viewStateItemListFlow

    private val addViewStateItemListFlow = MutableStateFlow<List<AddOrModifyPropertyViewStateItem>>(emptyList())
    val addViewStateItemList: StateFlow<List<AddOrModifyPropertyViewStateItem>> = addViewStateItemListFlow

    val propertyAddStatus = MutableLiveData<Long?>()
    val address = MutableLiveData<String?>()
    val latLong = MutableLiveData<String?>()
    val errorMessage = MutableLiveData<String?>()

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

    fun addNewProperty(addOrModifyPropertyViewState: AddOrModifyPropertyViewState) {
        viewModelScope.launch {
            val agentEntity = getRealEstateAgentUseCase.invoke()
            Log.d("ViewModelDebug", "ADD_PROPERTY_CALLED")

            if (agentEntity != null) {
                val propertyEntity = PropertyEntity(
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

                // Insert property and post the newId to LiveData
                val newIdDeferred = async { insertPropertyUseCase.invoke(propertyEntity) }
                val newId = newIdDeferred.await()
                propertyAddStatus.postValue(newId)

                if (newId != null) {
                    // Add Image and description
                    addViewStateItemListFlow.value.forEach { viewStateItem ->
                        if (viewStateItem is AddOrModifyPropertyViewStateItem.Photo) {
                            val photoEntity = PhotoEntity(
                                id = viewStateItem.id,
                                propertyId = newId,
                                description = viewStateItem.description,
                                photoUrl = viewStateItem.photoUrl
                            )
                            insertPhotoUseCase.invoke(photoEntity)
                        }
                    }

                    // Add point of interests
                    addOrModifyPropertyViewState.pointsOfInterest.split(",").forEach {
                        insertPointOfInterestUseCase.invoke(
                            PointOfInterestEntity(propertyId = newId, poi = it.trim())
                        )
                    }
                }
            }
        }
    }

    fun addImageAndDescription(uri: String, description: String) {
        viewModelScope.launch {
            val newAddOrModifyPropertyViewStateItem = AddOrModifyPropertyViewStateItem.Photo(
                id = 0,
                propertyId = 0,
                description = description,
                photoUrl = uri,
                onDeleteEvent = EquatableCallbackWithParam { position ->
                    viewModelScope.launch {
                        val currentList = addViewStateItemListFlow.value.toMutableList()
                        if (position in currentList.indices) {
                            currentList.removeAt(position)
                            addViewStateItemListFlow.emit(currentList)
                        }
                    }
                }
            )

            val currentList = addViewStateItemListFlow.value
            val newList = currentList.toMutableList().apply { add(newAddOrModifyPropertyViewStateItem) }

            addViewStateItemListFlow.emit(newList)
        }
    }

    val viewStateFlow: Flow<AddOrModifyPropertyViewState> = flow {
        val propertyId = getSelectedPropertyIdUseCase().value
        if (propertyId != null && propertyId > 0) {
            getPropertyWithPhotoAndPOIUseCase.invoke(propertyId).collect { propertyWithPhotosAndPOIEntity ->
                emit(mapEntityToViewState(propertyWithPhotosAndPOIEntity))
            }
        }
    }

    fun updateProperty(modifyPropertyViewState: AddOrModifyPropertyViewState) {
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
        modifyPropertyViewState: AddOrModifyPropertyViewState,
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
        modifyPropertyViewState: AddOrModifyPropertyViewState,
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
            pointsOfInterest = entity.pointsOfInterest.joinToString(",") { it.poi },
        )
    }

    private fun mapPhotoEntityToViewStateItem(entity: PhotoEntity): AddOrModifyPropertyViewStateItem.Photo {
        return AddOrModifyPropertyViewStateItem.Photo(
            id = entity.id,
            propertyId = entity.propertyId,
            description = entity.description,
            photoUrl = entity.photoUrl,
            onDeleteEvent = EquatableCallbackWithParam {
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

    fun initializePlaces(context: Context) {
        if (!Places.isInitialized()) {
            Places.initialize(context, MAPS_API_KEY)
        }
    }

    fun handleActivityResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                address.value = place.address
                place.latLng?.let { latLng ->
                    latLong.value = "${latLng.latitude},${latLng.longitude}"
                }
            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                errorMessage.value = status.statusMessage
            }

            Activity.RESULT_CANCELED -> {
                address.value = null
            }
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
