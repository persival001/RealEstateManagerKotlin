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
        isSold: Boolean,
        week: Boolean,
        month: Boolean,
        year: Boolean,
        poi: List<String>
    ) {
        val searchEntity = SearchEntity(
            type = if (type.isNullOrEmpty()) null else type,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minArea = minArea,
            maxArea = maxArea,
            isSold = isSold,
            entryDate = chipSelectedForSearchedDate(week, month, year),
            poi = null
        )
        setSearchedPropertiesUseCase.invoke(searchEntity)
    }

    private fun chipSelectedForSearchedDate(week: Boolean, month: Boolean, year: Boolean): String? {
        return when {
            week -> 1.toString()
            month -> 2.toString()
            year -> 3.toString()
            else -> null
        }
    }

    private fun chipSelectedForPoi(poi: List<String>): List<String> {
        return poi.filter { it != "None" }
    }

    fun onResetFilter() {
        setSearchedPropertiesUseCase.invoke(null)
    }

}
