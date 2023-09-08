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

    fun setSearchedProperties(viewState: SearchViewState) {
        val entity = viewState.toEntity()
        setSearchedPropertiesUseCase.invoke(entity)
    }

    private fun SearchViewState.toEntity(): SearchEntity {
        return SearchEntity(
            type = this.type,
            minPrice = this.minPrice,
            maxPrice = this.maxPrice,
            minArea = this.minArea,
            maxArea = this.maxArea,
            pois = this.pois,
            date = this.date
        )
    }

}
