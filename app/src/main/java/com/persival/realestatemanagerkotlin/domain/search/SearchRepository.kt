package com.persival.realestatemanagerkotlin.domain.search

import com.persival.realestatemanagerkotlin.domain.search.model.Search
import kotlinx.coroutines.flow.StateFlow

interface SearchRepository {

    val selectedFilter: StateFlow<Search?>

    fun setFilter(search: Search?)

}
