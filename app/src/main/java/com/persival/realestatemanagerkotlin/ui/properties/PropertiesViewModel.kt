package com.persival.realestatemanagerkotlin.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForCurrencyConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.database.SynchronizeDatabaseUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
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
    private val getSavedStateForCurrencyConversionButtonUseCase: GetSavedStateForCurrencyConversionButtonUseCase,
) : ViewModel() {

    private val propertiesViewStateItem = MutableLiveData<List<PropertyViewStateItem>>()
    val properties: LiveData<List<PropertyViewStateItem>> = propertiesViewStateItem
    private val propertyIdSelected = MutableLiveData<Long?>()
    private var currentCurrency: String = "USD"

    init {
        getSavedStateForCurrencyConversionButtonUseCase.invoke()
            .onEach {
                updatePropertyPrices()
            }
            .launchIn(viewModelScope)
        displayProperties()
    }

    private fun displayProperties() {
        viewModelScope.launch {
            val isConversionEnabled = getSavedStateForCurrencyConversionButtonUseCase.invoke().first()
            getAllPropertiesWithPhotosAndPOIUseCase.invoke()
                .collect { propertiesList ->
                    val viewStateItems = propertiesList.map { property ->
                        transformToViewState(property, isConversionEnabled)
                    }
                    propertiesViewStateItem.value = viewStateItems
                }
        }
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

    private fun calculatePriceAndLocale(currentPrice: Int, isConversionEnabled: Boolean): Pair<Int, Locale> {
        return when {
            isConversionEnabled && currentCurrency == "USD" -> {
                currentCurrency = "EUR"
                Utils.convertDollarToEuro(currentPrice) to Locale.FRANCE
            }

            isConversionEnabled && currentCurrency == "EUR" -> {
                currentPrice to Locale.FRANCE
            }

            !isConversionEnabled && currentCurrency == "EUR" -> {
                currentCurrency = "USD"
                Utils.convertEuroToDollar(currentPrice) to Locale.US
            }

            else -> {
                currentPrice to Locale.US
            }
        }
    }

    private fun getFormattedPrice(currentPrice: Int, isConversionEnabled: Boolean): String {
        val (convertedPrice, locale) = calculatePriceAndLocale(currentPrice, isConversionEnabled)
        val currencyFormat = NumberFormat.getCurrencyInstance(locale)
        currencyFormat.maximumFractionDigits = 0
        return currencyFormat.format(convertedPrice)
    }

    private fun updatePropertyPrices() {
        viewModelScope.launch {
            try {
                val isConversionEnabled = getSavedStateForCurrencyConversionButtonUseCase.invoke().first()
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

    /*fun synchronizeDatabase() {
        synchronizeDatabaseUseCase.invoke()
    }*/

}