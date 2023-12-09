package com.persival.realestatemanagerkotlin.data.search

import com.persival.realestatemanagerkotlin.domain.search.SearchRepository
import com.persival.realestatemanagerkotlin.domain.search.model.Search
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchDataRepository @Inject constructor() : SearchRepository {

    private val selectedFilterStateFlow = MutableStateFlow<Search?>(null)

    override val selectedFilter: StateFlow<Search?> get() = selectedFilterStateFlow

    override fun setFilter(search: Search?) {
        selectedFilterStateFlow.value = search
    }

}
