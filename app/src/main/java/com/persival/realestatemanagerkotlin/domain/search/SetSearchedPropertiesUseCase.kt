package com.persival.realestatemanagerkotlin.domain.search

import com.persival.realestatemanagerkotlin.domain.search.model.Search
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetSearchedPropertiesUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    fun invoke(search: Search?) {
        searchRepository.setFilter(search)
    }
}