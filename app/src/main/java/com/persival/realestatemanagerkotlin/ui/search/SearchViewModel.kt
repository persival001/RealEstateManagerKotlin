package com.persival.realestatemanagerkotlin.ui.search

import androidx.lifecycle.ViewModel
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.domain.search.SetSearchedPropertiesUseCase
import com.persival.realestatemanagerkotlin.domain.search.model.Search
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val setSearchedPropertiesUseCase: SetSearchedPropertiesUseCase
) : ViewModel() {

    fun setSearchCriteria(
        type: String?,
        minPrice: Int?,
        maxPrice: Int?,
        minArea: Int?,
        maxArea: Int?,
        isSold: Boolean?,
        checkedChipId: Int,
        poi: List<String>
    ) {
        val search = Search(
            type = if (type.isNullOrEmpty()) null else type,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minArea = minArea,
            maxArea = maxArea,
            isSold = isSold,
            timeFilter = getAgeOfPropertyRange(checkedChipId),
            poi = poi
        )
        setSearchedPropertiesUseCase.invoke(search)
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
