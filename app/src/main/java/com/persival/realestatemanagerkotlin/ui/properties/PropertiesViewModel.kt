package com.persival.realestatemanagerkotlin.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val getAllPropertiesWithPhotosAndPOIUseCase: GetAllPropertiesWithPhotosAndPOIUseCase,
) : ViewModel() {

    private val propertiesViewStateItem = MutableLiveData<List<PropertyViewStateItem>>()
    val properties: LiveData<List<PropertyViewStateItem>> = propertiesViewStateItem

    init {
        loadProperties()
    }

    private fun loadProperties() {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getAllPropertiesWithPhotosAndPOIUseCase.invoke().collect { properties ->
                val viewStateItems = properties
                    .map { propertyWithPhotosAndPOI ->
                        PropertyViewStateItem(
                            id = propertyWithPhotosAndPOI.property.id!!,
                            type = propertyWithPhotosAndPOI.property.type,
                            address = propertyWithPhotosAndPOI.property.address,
                            price = getFormattedPrice(propertyWithPhotosAndPOI.property.price),
                            pictureUri = propertyWithPhotosAndPOI.photos.firstOrNull()?.photoUrl
                                ?: "",
                            isSold = propertyWithPhotosAndPOI.property.isSold
                        )
                    }
                    .sortedBy { it.isSold }

                withContext(Dispatchers.Main) {
                    propertiesViewStateItem.value = viewStateItems
                }
            }
        }
    }

    private fun getFormattedPrice(price: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.maximumFractionDigits = 0
        return format.format(price)
    }

}