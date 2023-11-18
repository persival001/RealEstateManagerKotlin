package com.persival.realestatemanagerkotlin.ui.search

import androidx.lifecycle.ViewModel
import com.persival.realestatemanagerkotlin.domain.search.SearchEntity
import com.persival.realestatemanagerkotlin.domain.search.SetSearchedPropertiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val setSearchedPropertiesUseCase: SetSearchedPropertiesUseCase
) : ViewModel() {

    fun setSearchCriteria(
        type: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        minArea: Int? = null,
        maxArea: Int? = null,
        minRooms: Int? = null,
        maxRooms: Int? = null,
        isSold: Boolean? = null,
        latLng: String? = null,
        entryDate: String? = null,
        poi: String? = null
    ) {
        val searchEntity = SearchEntity(
            type = type,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minArea = minArea,
            maxArea = maxArea,
            minRooms = minRooms,
            maxRooms = maxRooms,
            isSold = isSold,
            latLng = latLng,
            entryDate = entryDate,
            poi = poi
        )
        if (searchEntity == SearchEntity(
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        ) {
            setSearchedPropertiesUseCase.invoke(null)
        } else
            setSearchedPropertiesUseCase.invoke(searchEntity)
    }

    fun onResetFilter() {
        setSearchedPropertiesUseCase.invoke(null)
    }

}
