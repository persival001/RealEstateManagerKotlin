package com.persival.realestatemanagerkotlin.domain.search

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetSearchedPropertiesUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    fun invoke(searchEntity: SearchEntity) {
        searchRepository.setFilter(searchEntity)
    }
}