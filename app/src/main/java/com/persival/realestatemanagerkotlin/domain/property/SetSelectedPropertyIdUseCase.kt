package com.persival.realestatemanagerkotlin.domain.property

import javax.inject.Inject

class SetSelectedPropertyIdUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository,
) {
    operator fun invoke(id: Long?) {
        propertyRepository.setSelectedId(id)
    }
}