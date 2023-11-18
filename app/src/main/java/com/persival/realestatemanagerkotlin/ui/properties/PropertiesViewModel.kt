package com.persival.realestatemanagerkotlin.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.persival.realestatemanagerkotlin.domain.conversion.GetSavedStateForCurrencyConversionButtonUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
import com.persival.realestatemanagerkotlin.domain.search.GetActiveSearchFilterUseCase
import com.persival.realestatemanagerkotlin.domain.search.GetSearchedPropertiesUseCase
import com.persival.realestatemanagerkotlin.domain.search.SetSearchedPropertiesUseCase
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val getAllPropertiesWithPhotosAndPOIUseCase: GetAllPropertiesWithPhotosAndPOIUseCase,
    private val setSelectedPropertyIdUseCase: SetSelectedPropertyIdUseCase,
    getSavedStateForCurrencyConversionButtonUseCase: GetSavedStateForCurrencyConversionButtonUseCase,
    private val getActiveSearchFilterUseCase: GetActiveSearchFilterUseCase,
    private val getSearchedPropertiesUseCase: GetSearchedPropertiesUseCase,
    private val setSearchedPropertiesUseCase: SetSearchedPropertiesUseCase,
) : ViewModel() {

    private val propertyIdSelected = MutableLiveData<Long?>()
    private val isConversionEnabledLiveData: LiveData<Boolean> =
        getSavedStateForCurrencyConversionButtonUseCase.invoke().asLiveData()

    val properties: LiveData<List<PropertyViewStateItem>> = MediatorLiveData<List<PropertyViewStateItem>>().apply {
        var currentProperties: List<PropertyWithPhotosAndPOIEntity> = emptyList()
        var isConversionEnabled = false

        addSource(isConversionEnabledLiveData) { enabled ->
            isConversionEnabled = enabled
            value = currentProperties.map { transformToViewState(it, isConversionEnabled) }
        }

        addSource(getActiveSearchFilterUseCase.invoke().flatMapLatest { filter ->
            filter?.let { getSearchedPropertiesUseCase.invoke(it) }
                ?: getAllPropertiesWithPhotosAndPOIUseCase.invoke()
        }.asLiveData()) { propertiesList ->
            currentProperties = propertiesList
            value = propertiesList.map { transformToViewState(it, isConversionEnabled) }
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
        return if (isConversionEnabled) {
            Utils.convertDollarToEuro(currentPrice) to Locale.FRANCE
        } else {
            currentPrice to Locale.US
        }
    }

    private fun getFormattedPrice(currentPrice: Int, isConversionEnabled: Boolean): String {
        val (convertedPrice, locale) = calculatePriceAndLocale(currentPrice, isConversionEnabled)
        val currencyFormat = NumberFormat.getCurrencyInstance(locale)
        currencyFormat.maximumFractionDigits = 0
        return currencyFormat.format(convertedPrice)
    }

    private fun getFormattedPoi(pointsOfInterest: List<PointOfInterestEntity>): String {
        return pointsOfInterest.joinToString(separator = ", ") { it.poi }
    }

    fun updateSelectedPropertyId(id: Long?) {
        setSelectedPropertyIdUseCase(id)
        propertyIdSelected.value = id
    }

    fun onResetFilter() {
        setSearchedPropertiesUseCase.invoke(null)
    }

}