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
import com.persival.realestatemanagerkotlin.domain.search.GetActiveSearchFilterUseCase
import com.persival.realestatemanagerkotlin.domain.search.SearchEntity
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
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
    private val getActiveSearchFilterUseCase: GetActiveSearchFilterUseCase,
) : ViewModel() {

    private val propertiesViewStateItem = MutableLiveData<List<PropertyViewStateItem>>()
    val properties: LiveData<List<PropertyViewStateItem>> = propertiesViewStateItem
    private val propertyIdSelected = MutableLiveData<Long?>()

    init {
        combineFiltersWithProperties()
    }

    private fun combineFiltersWithProperties() {
        viewModelScope.launch {
            val filterFlow = getActiveSearchFilterUseCase.invoke()
            val propertiesFlow = getAllPropertiesWithPhotosAndPOIUseCase.invoke()

            combine(filterFlow, propertiesFlow) { filter, properties ->
                var filteredProperties = properties.filter {
                    it.meetsFilterCriteria(filter)
                }

                filteredProperties = when (filter?.date) {
                    "Old first" -> filteredProperties.sortedBy { it.property.entryDate }
                    "Recent first" -> filteredProperties.sortedByDescending { it.property.entryDate }
                    else -> filteredProperties
                }

                filteredProperties.map { propertyWithPhotosAndPOI ->
                    transformToViewState(propertyWithPhotosAndPOI)
                }
            }.collect {
                propertiesViewStateItem.value = it
            }
        }
    }

    private fun PropertyWithPhotosAndPOIEntity.meetsFilterCriteria(filter: SearchEntity?): Boolean {
        if (filter == null) return true

        if (this.property.type != filter.type) return false
        
        if (this.property.price < filter.minPrice) return false
        if (filter.maxPrice != Int.MAX_VALUE && this.property.price > filter.maxPrice) return false

        if (this.property.area < filter.minArea) return false
        if (filter.maxArea != Int.MAX_VALUE && this.property.area > filter.maxArea) return false

        filter.pois.let { poisFilter: String ->
            val propertyPois: List<String> = this.pointsOfInterest.map { it.poi }
            val filterPOIs = poisFilter.split(",")
            if (!filterPOIs.all { it in propertyPois }) return false
        }

        return true
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

    fun synchronizeDatabase() {
        synchronizeDatabaseUseCase.invoke()
    }

}