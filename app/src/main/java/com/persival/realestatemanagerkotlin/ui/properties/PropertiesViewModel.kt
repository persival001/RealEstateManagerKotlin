package com.persival.realestatemanagerkotlin.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.persival.realestatemanagerkotlin.domain.conversion.IsEuroConversionEnabledUseCase
import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import com.persival.realestatemanagerkotlin.domain.property.SetSelectedPropertyIdUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.GetAllPropertiesWithPhotosAndPOIUseCase
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model.PropertyWithPhotosAndPOI
import com.persival.realestatemanagerkotlin.domain.search.GetActiveSearchFilterUseCase
import com.persival.realestatemanagerkotlin.domain.search.GetSearchedPropertiesUseCase
import com.persival.realestatemanagerkotlin.domain.search.SetSearchedPropertiesUseCase
import com.persival.realestatemanagerkotlin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val getAllPropertiesWithPhotosAndPOIUseCase: GetAllPropertiesWithPhotosAndPOIUseCase,
    private val setSelectedPropertyIdUseCase: SetSelectedPropertyIdUseCase,
    isEuroConversionEnabledUseCase: IsEuroConversionEnabledUseCase,
    private val getActiveSearchFilterUseCase: GetActiveSearchFilterUseCase,
    private val getSearchedPropertiesUseCase: GetSearchedPropertiesUseCase,
    private val setSearchedPropertiesUseCase: SetSearchedPropertiesUseCase,
) : ViewModel() {

    private val propertyIdSelected = MutableLiveData<Long?>()

    val properties: LiveData<List<PropertyViewStateItem>> = liveData {
        combine(
            isEuroConversionEnabledUseCase.invoke(),
            getActiveSearchFilterUseCase.invoke().flatMapLatest { filter ->
                filter?.let { getSearchedPropertiesUseCase.invoke(it) }
                    ?: getAllPropertiesWithPhotosAndPOIUseCase.invoke()
            },
        ) { isConversionEnabled, propertiesList ->
            propertiesList.map { propertyWithPhotosAndPOIEntity ->
                transformToViewState(propertyWithPhotosAndPOIEntity, isConversionEnabled)
            }
        }.collectLatest { emit(it) }
    }

    private fun transformToViewState(
        propertyWithPhotosAndPOI: PropertyWithPhotosAndPOI,
        isConversionEnabled: Boolean
    ): PropertyViewStateItem {
        val formattedPrice = getFormattedPrice(propertyWithPhotosAndPOI.property.price, isConversionEnabled)

        return PropertyViewStateItem(
            id = propertyWithPhotosAndPOI.property.id,
            type = propertyWithPhotosAndPOI.property.type,
            address = propertyWithPhotosAndPOI.property.address,
            latLng = propertyWithPhotosAndPOI.property.latLng,
            price = formattedPrice,
            rooms = propertyWithPhotosAndPOI.property.rooms.toString(),
            surface = propertyWithPhotosAndPOI.property.area.toString(),
            bathrooms = propertyWithPhotosAndPOI.property.bathrooms.toString(),
            bedrooms = propertyWithPhotosAndPOI.property.bedrooms.toString(),
            poi = getFormattedPoi(propertyWithPhotosAndPOI.pointsOfInterest),
            pictureUri = propertyWithPhotosAndPOI.photos.firstOrNull()?.photoUrl ?: "",
            isSold = propertyWithPhotosAndPOI.property.isSold
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

    private fun getFormattedPoi(pointsOfInterest: List<PointOfInterest>): String {
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