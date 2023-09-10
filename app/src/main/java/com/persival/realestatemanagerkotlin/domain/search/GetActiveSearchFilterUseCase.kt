package com.persival.realestatemanagerkotlin.domain.search

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetActiveSearchFilterUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    fun invoke(): StateFlow<SearchEntity?> = searchRepository.selectedFilter
}
