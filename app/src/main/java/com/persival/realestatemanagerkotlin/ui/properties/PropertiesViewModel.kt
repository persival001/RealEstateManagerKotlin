package com.persival.realestatemanagerkotlin.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForCurrencyConversionButton
import com.persival.realestatemanagerkotlin.domain.database.SynchronizeDatabaseUseCase
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val getAllPropertiesWithPhotosAndPOIUseCase: GetAllPropertiesWithPhotosAndPOIUseCase,
    private val setSelectedPropertyIdUseCase: SetSelectedPropertyIdUseCase,
    private val synchronizeDatabaseUseCase: SynchronizeDatabaseUseCase,
    private val getSavedStateForCurrencyConversionButton: GetSavedStateForCurrencyConversionButton,
) : ViewModel() {

    private val propertiesViewStateItem = MutableLiveData<List<PropertyViewStateItem>>()
    val properties: LiveData<List<PropertyViewStateItem>> = propertiesViewStateItem
    private val propertyIdSelected = MutableLiveData<Long?>()
    val showNotificationEvent = MutableLiveData<Boolean>()

    init {
        combineFiltersWithProperties("")
    }

    fun combineFiltersWithProperties(searchQuery: String) {
        viewModelScope.launch {
            val propertiesFlow = getAllPropertiesWithPhotosAndPOIUseCase.invoke()

            propertiesFlow.map { properties ->
                if (searchQuery.isBlank()) {
                    // If search query is empty, return all properties
                    properties.map { transformToViewState(it) }
                } else {
                    // Filter properties based on search query
                    properties.filter { propertyWithPhotosAndPOI ->
                        val property = propertyWithPhotosAndPOI.property

                        // Check if the search query appears in any of the relevant fields
                        property.type.contains(searchQuery, ignoreCase = true) ||
                                property.address.contains(searchQuery, ignoreCase = true) ||
                                property.area.toString().contains(searchQuery, ignoreCase = true) ||
                                property.rooms.toString().contains(searchQuery, ignoreCase = true) ||
                                property.bathrooms.toString().contains(searchQuery, ignoreCase = true) ||
                                property.bedrooms.toString().contains(searchQuery, ignoreCase = true) ||
                                property.description.contains(searchQuery, ignoreCase = true) ||
                                property.price.toString().contains(searchQuery, ignoreCase = true) ||
                                property.agentName.contains(searchQuery, ignoreCase = true)
                    }.map { propertyWithPhotosAndPOI ->
                        transformToViewState(propertyWithPhotosAndPOI)
                    }
                }
            }.collect {
                propertiesViewStateItem.value = it
            }
        }
    }

    private fun transformToViewState(propertyWithPhotosAndPOIEntity: PropertyWithPhotosAndPOIEntity): PropertyViewStateItem {
        return PropertyViewStateItem(
            id = propertyWithPhotosAndPOIEntity.property.id,
            type = propertyWithPhotosAndPOIEntity.property.type,
            address = propertyWithPhotosAndPOIEntity.property.address,
            price = getFormattedPrice(propertyWithPhotosAndPOIEntity.property.price),
            pictureUri = propertyWithPhotosAndPOIEntity.photos.firstOrNull()?.photoUrl ?: "",
            isSold = propertyWithPhotosAndPOIEntity.property.isSold
        )
    }

    private fun getFormattedPrice(price: Int): String {
        val isConversionEnabled = getSavedStateForCurrencyConversionButton.invoke()

        return if (isConversionEnabled) {
            val euroValue = Utils.convertDollarToEuro(price)
            formatPriceAsEuro(euroValue)
        } else {
            formatPriceAsDollar(price)
        }
    }

    private fun formatPriceAsEuro(price: Int): String {
        val euroFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE)
        euroFormat.maximumFractionDigits = 0
        return euroFormat.format(price)
    }

    private fun formatPriceAsDollar(price: Int): String {
        val dollarFormat = NumberFormat.getCurrencyInstance(Locale.US)
        dollarFormat.maximumFractionDigits = 0
        return dollarFormat.format(price)
    }

    fun updatePropertyPrices() {
        val currentProperties = propertiesViewStateItem.value ?: return
        val updatedProperties = currentProperties.map {
            val cleanedPrice = it.price.replace("\\D".toRegex(), "")
            val originalPrice = cleanedPrice.toInt()
            if (it.price.contains("$")) {
                val euroValue = Utils.convertDollarToEuro(originalPrice)
                it.copy(price = formatPriceAsEuro(euroValue))
            } else {
                val dollarValue = Utils.convertEuroToDollar(originalPrice)
                it.copy(price = formatPriceAsDollar(dollarValue))
            }
        }

        propertiesViewStateItem.value = updatedProperties
    }

    fun updateSelectedPropertyId(id: Long?) {
        setSelectedPropertyIdUseCase(id)
        propertyIdSelected.value = id
    }

    private fun showNotificationForNewProperty() {
        showNotificationEvent.value = true
    }

    fun synchronizeDatabase() {
        synchronizeDatabaseUseCase.invoke()
    }

}