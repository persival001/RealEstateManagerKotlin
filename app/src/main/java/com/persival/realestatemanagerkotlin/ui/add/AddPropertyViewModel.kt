package com.persival.realestatemanagerkotlin.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.data.local_database.LocalDatabaseRepository
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.user.GetRealEstateAgentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val getRealEstateAgentUseCase: GetRealEstateAgentUseCase,
    private val repository: LocalDatabaseRepository
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
                    true,
                    entryDate,
                    saleDate,
                    it.name
                )
            }

            val propertyId: Long? = propertyEntity?.let { repository.insertProperty(it) }


            imageUris.forEachIndexed { index, uri ->
                val photoEntity = propertyId?.let {
                    PhotoEntity(
                        propertyId = it,
                        description = photoDescriptions.getOrNull(index) ?: "",
                        url = uri
                    )
                }
                if (photoEntity != null) {
                    repository.insertPhoto(photoEntity)
                }
            }
        }
    }
}
