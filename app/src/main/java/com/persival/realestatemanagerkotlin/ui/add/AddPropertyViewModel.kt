package com.persival.realestatemanagerkotlin.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.InsertPhotoUseCase
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.poi.InsertPointOfInterestUseCase
import com.persival.realestatemanagerkotlin.domain.property.InsertPropertyUseCase
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
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
    private val insertPointOfInterestUseCase: InsertPointOfInterestUseCase
) : ViewModel() {

    fun addNewProperty(
        type: String,
        address: String,
        area: Int,
        rooms: Int,
        bathrooms: Int,
        bedrooms: Int,
        description: String,
        price: Int,
        entryDate: String,
        saleDate: String?,
        imageUris: List<String>,
        photoDescriptions: List<String>
    ) {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            val propertyEntity = getRealEstateAgentUseCase.invoke()?.let {
                PropertyEntity(
                    null,
                    type,
                    address,
                    area,
                    rooms,
                    bathrooms,
                    bedrooms,
                    description,
                    price,
                    isThePropertyForSale(saleDate),
                    entryDate,
                    saleDate,
                    it.name
                )
            }

            val propertyId: Long? = propertyEntity?.let { insertPropertyUseCase.invoke(it) }


            imageUris.forEachIndexed { index, uri ->
                val photoEntity = propertyId?.let {
                    PhotoEntity(
                        propertyId = it,
                        description = photoDescriptions.getOrNull(index) ?: "",
                        photoUrl = uri
                    )
                }
                if (photoEntity != null) {
                    insertPhotoUseCase.invoke(photoEntity)
                }
            }
        }
    }

    private fun isThePropertyForSale(saleDate: String?): Boolean {
        return !(saleDate == null || saleDate == "")
    }
}
