package com.persival.realestatemanagerkotlin.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val getAllPropertiesWithPhotosAndPOIUseCase: GetAllPropertiesWithPhotosAndPOIUseCase
) : ViewModel() {

    private val _properties = MutableLiveData<List<PropertyViewStateItem>>()
    val properties: LiveData<List<PropertyViewStateItem>> = _properties

    init {
        loadProperties()
    }

    private fun loadProperties() {
        viewModelScope.launch {
            val properties = getAllPropertiesWithPhotosAndPOIUseCase.invoke().toList()
            _properties.value = properties.map { propertyWithPhotosAndPOI ->
                PropertyViewStateItem(
                    id = propertyWithPhotosAndPOI.property.id!!, // !! because we know it's not null
                    type = propertyWithPhotosAndPOI.property.type,
                    address = propertyWithPhotosAndPOI.property.address,
                    price = propertyWithPhotosAndPOI.property.price.toString(),
                    pictureUri = propertyWithPhotosAndPOI.photos.firstOrNull()?.photoUrl
                        ?: "",
                    isSold = propertyWithPhotosAndPOI.property.isSold
                )
            }
        }
    }


}
