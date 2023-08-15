package com.persival.realestatemanagerkotlin.domain.property

import javax.inject.Inject

class GetSelectedPropertyIdUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository,
) {
    fun invoke(): Long? {
        return propertyRepository.getSelectedId()
    }
}