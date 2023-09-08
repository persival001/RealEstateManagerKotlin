package com.persival.realestatemanagerkotlin.data.search

import com.persival.realestatemanagerkotlin.domain.search.SearchEntity
import com.persival.realestatemanagerkotlin.domain.search.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchDataRepository @Inject constructor() : SearchRepository {

    private val selectedFilterStateFlow = MutableStateFlow<SearchEntity?>(null)

    override val selectedFilter: StateFlow<SearchEntity?> get() = selectedFilterStateFlow

    override fun setFilter(searchEntity: SearchEntity?) {
        selectedFilterStateFlow.value = searchEntity
    }

}
