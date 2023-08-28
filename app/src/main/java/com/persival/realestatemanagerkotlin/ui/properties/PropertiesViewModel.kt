package com.persival.realestatemanagerkotlin.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForCurrencyConversionButton
import com.persival.realestatemanagerkotlin.domain.database.SynchronizeDatabaseUseCase
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        loadProperties()
    }

    private fun loadProperties() {
        viewModelScope.launch {

            getAllPropertiesWithPhotosAndPOIUseCase.invoke().collect { properties ->
                val viewStateItems = properties.map { propertyWithPhotosAndPOI ->
                    PropertyViewStateItem(
                        id = propertyWithPhotosAndPOI.property.id,
                        type = propertyWithPhotosAndPOI.property.type,
                        address = propertyWithPhotosAndPOI.property.address,
                        price = getFormattedPrice(propertyWithPhotosAndPOI.property.price),
                        pictureUri = propertyWithPhotosAndPOI.photos.firstOrNull()?.photoUrl ?: "",
                        isSold = propertyWithPhotosAndPOI.property.isSold
                    )
                }.sortedBy { it.isSold }

                withContext(Dispatchers.Main) {
                    propertiesViewStateItem.value = viewStateItems
                }
            }
        }
    }

    private fun getFormattedPrice(price: Int): String {
        val isConversionEnabled = getSavedStateForCurrencyConversionButton.invoke()
        return if (isConversionEnabled) {
            val euroPrice = Utils.convertDollarToEuro(price)
            NumberFormat.getCurrencyInstance(Locale.FRANCE).format(euroPrice)
        } else {
            val dollarPrice = Utils.convertEuroToDollar(price)
            NumberFormat.getCurrencyInstance(Locale.US).format(dollarPrice)
        }
    }

    fun updatePropertyPrices() {
        val currentProperties = propertiesViewStateItem.value ?: return
        val updatedProperties = currentProperties.map {
            it.copy(price = getFormattedPrice(it.price.toInt()))
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