package com.persival.realestatemanagerkotlin.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForCurrencyConversionButton
import com.persival.realestatemanagerkotlin.domain.database.SynchronizeDatabaseUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

    init {
        combineFiltersWithProperties(
            "", areaSearch = false, roomSearch = false, priceSearch = false,
            soldSearch = false
        )
        observeCurrencyChanges()
    }

    private fun observeCurrencyChanges() {
        getSavedStateForCurrencyConversionButton.invoke()
            .onEach {
                updatePropertyPrices()
            }
            .launchIn(viewModelScope)
    }

    fun combineFiltersWithProperties(
        searchQuery: String,
        areaSearch: Boolean,
        roomSearch: Boolean,
        priceSearch: Boolean,
        soldSearch: Boolean,
    ) {
        viewModelScope.launch {
            val propertiesFlow = getAllPropertiesWithPhotosAndPOIUseCase.invoke()
            val isConversionEnabled = getSavedStateForCurrencyConversionButton.invoke().first()

            propertiesFlow.map { properties ->
                val finalProperties = if (searchQuery.isBlank()) {
                    // If searchQuery is blank, sort properties using sortProperties
                    sortProperties(properties, areaSearch, roomSearch, priceSearch, soldSearch, isConversionEnabled)
                } else {
                    // Otherwise, apply the existing filtering logic
                    val filteredProperties = properties.filter { propertyWithPhotosAndPOI ->
                        val property = propertyWithPhotosAndPOI.property
                        property.type.contains(searchQuery, ignoreCase = true) ||
                                property.address.contains(searchQuery, ignoreCase = true) ||
                                property.area.toString().contains(searchQuery, ignoreCase = true) ||
                                property.rooms.toString().contains(searchQuery, ignoreCase = true) ||
                                property.bathrooms.toString().contains(searchQuery, ignoreCase = true) ||
                                property.bedrooms.toString().contains(searchQuery, ignoreCase = true) ||
                                property.description.contains(searchQuery, ignoreCase = true) ||
                                property.price.toString().contains(searchQuery, ignoreCase = true) ||
                                property.agentName.contains(searchQuery, ignoreCase = true)
                    }.map { transformToViewState(it, isConversionEnabled) }
                    filteredProperties
                }

                finalProperties
            }.collect {
                propertiesViewStateItem.value = it
            }
        }
    }

    // Sort properties based on boolean flags and transform to ViewState
    private fun sortProperties(
        properties: List<PropertyWithPhotosAndPOIEntity>,
        areaSearch: Boolean,
        roomSearch: Boolean,
        priceSearch: Boolean,
        soldSearch: Boolean,
        isConversionEnabled: Boolean
    ): List<PropertyViewStateItem> {

        // Sort by isSold first
        val initialSorted = if (soldSearch) {
            properties.sortedBy { if (it.property.isSold) 0 else 1 }
        } else {
            properties.sortedBy { if (it.property.isSold) 1 else 0 }
        }

        // Apply additional sort based on other flags
        val finalSortedProperties = when {
            areaSearch -> initialSorted.sortedBy { it.property.area }
            roomSearch -> initialSorted.sortedBy { it.property.rooms }
            priceSearch -> initialSorted.sortedBy { it.property.price }
            else -> initialSorted
        }

        return finalSortedProperties.map { transformToViewState(it, isConversionEnabled) }

    }

    private fun transformToViewState(
        propertyWithPhotosAndPOIEntity: PropertyWithPhotosAndPOIEntity,
        isConversionEnabled: Boolean
    ): PropertyViewStateItem {
        val formattedPrice = getFormattedPrice(propertyWithPhotosAndPOIEntity.property.price, isConversionEnabled)

        return PropertyViewStateItem(
            id = propertyWithPhotosAndPOIEntity.property.id,
            type = propertyWithPhotosAndPOIEntity.property.type,
            address = propertyWithPhotosAndPOIEntity.property.address,
            latLng = propertyWithPhotosAndPOIEntity.property.latLng,
            price = formattedPrice,
            rooms = propertyWithPhotosAndPOIEntity.property.rooms.toString(),
            surface = propertyWithPhotosAndPOIEntity.property.area.toString(),
            bathrooms = propertyWithPhotosAndPOIEntity.property.bathrooms.toString(),
            bedrooms = propertyWithPhotosAndPOIEntity.property.bedrooms.toString(),
            poi = getFormattedPoi(propertyWithPhotosAndPOIEntity.pointsOfInterest),
            pictureUri = propertyWithPhotosAndPOIEntity.photos.firstOrNull()?.photoUrl ?: "",
            isSold = propertyWithPhotosAndPOIEntity.property.isSold
        )
    }

    private fun getFormattedPrice(price: Int, isConversionEnabled: Boolean): String {
        val (convertedPrice, locale) = if (isConversionEnabled) {
            Pair(Utils.convertDollarToEuro(price), Locale.FRANCE)
        } else {
            Pair(price, Locale.US)
        }

        val currencyFormat = NumberFormat.getCurrencyInstance(locale)
        currencyFormat.maximumFractionDigits = 0
        return currencyFormat.format(convertedPrice)
    }

    private fun updatePropertyPrices() {
        viewModelScope.launch {
            try {
                val isConversionEnabled = getSavedStateForCurrencyConversionButton.invoke().first()
                val currentProperties = propertiesViewStateItem.value ?: return@launch
                val updatedProperties = currentProperties.map {
                    val cleanedPrice = it.price.replace("\\D".toRegex(), "")
                    val originalPrice = cleanedPrice.toIntOrNull() ?: 0
                    val formattedPrice = getFormattedPrice(originalPrice, isConversionEnabled)
                    it.copy(price = formattedPrice)
                }
                propertiesViewStateItem.value = updatedProperties
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    private fun getFormattedPoi(pointsOfInterest: List<PointOfInterestEntity>): String {
        return pointsOfInterest.joinToString(separator = ", ") { it.poi }
    }

    fun updateSelectedPropertyId(id: Long?) {
        setSelectedPropertyIdUseCase(id)
        propertyIdSelected.value = id
    }

    fun synchronizeDatabase() {
        synchronizeDatabaseUseCase.invoke()
    }

}