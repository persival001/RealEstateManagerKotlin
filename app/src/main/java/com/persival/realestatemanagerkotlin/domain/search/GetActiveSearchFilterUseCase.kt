package com.persival.realestatemanagerkotlin.domain.search

import com.persival.realestatemanagerkotlin.domain.search.model.Search
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetActiveSearchFilterUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    fun invoke(): StateFlow<Search?> = searchRepository.selectedFilter
}
