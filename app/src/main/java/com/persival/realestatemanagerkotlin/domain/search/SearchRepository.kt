package com.persival.realestatemanagerkotlin.domain.search

import kotlinx.coroutines.flow.StateFlow

interface SearchRepository {

    val selectedFilter: StateFlow<SearchEntity?>

    fun setFilter(searchEntity: SearchEntity?)

}
