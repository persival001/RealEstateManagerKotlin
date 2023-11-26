package com.persival.realestatemanagerkotlin.ui.search

import androidx.lifecycle.ViewModel
import com.persival.realestatemanagerkotlin.R
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
        isSold: Boolean? = null,
        checkedChipId: Int,
        poi: List<String>? = null
    ) {
        val searchEntity = SearchEntity(
            type = if (type.isNullOrEmpty()) null else type,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minArea = minArea,
            maxArea = maxArea,
            isSold = isSold,
            timeFilter = getAgeOfPropertyRange(checkedChipId),
            poi = if (poi.isNullOrEmpty()) null else poi
        )
        setSearchedPropertiesUseCase.invoke(searchEntity)
    }

    fun onResetFilter() {
        setSearchedPropertiesUseCase.invoke(null)
    }

    private fun getAgeOfPropertyRange(checkedChipId: Int): String? =
        when (checkedChipId) {
            R.id.lessThanAMonthChip -> AgeOfPropertyState.LESS_THAN_A_MONTH.timeFilter
            R.id.lessThanSixMonthChip -> AgeOfPropertyState.LESS_THAN_SIX_MONTHS.timeFilter
            R.id.lessThanAYearChip -> AgeOfPropertyState.LESS_THAN_A_YEAR.timeFilter
            else -> null
        }

}
