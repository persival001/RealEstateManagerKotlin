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
        isSold: Boolean,
        checkedChipId: Int,
        poi: List<String>
    ) {
        val searchEntity = SearchEntity(
            type = if (type.isNullOrEmpty()) null else type,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minArea = minArea,
            maxArea = maxArea,
            isSold = isSold,
            timeFilter = getAgeOfPropertyRange(checkedChipId),
            poi = null
        )
        setSearchedPropertiesUseCase.invoke(searchEntity)
    }

    private fun chipSelectedForPoi(poi: List<String>): List<String> {
        return poi.filter { it != "None" }
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

    /* private fun mapSearchViewStateToSearchEntity(searchViewState: SearchViewState): SearchEntity =
         SearchEntity(
             type = searchViewState.type,
             minPrice = searchViewState.minPrice,
             maxPrice = searchViewState.maxPrice,
             minArea = searchViewState.minArea,
             maxArea = searchViewState.maxArea,
             isSold = searchViewState.isSold,
             entryDate = searchViewState.entryDate,
             poi = chipSelectedForPoi(searchViewState.poi)
         )*/

}
